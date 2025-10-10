package processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import lib.homies.framework.command.annotations.Async;
import lib.homies.framework.command.annotations.Command;
import lib.homies.framework.command.annotations.SubCommand;
import lib.homies.framework.command.annotations.TabComplete;
import lib.homies.framework.command.context.SubcommandInfo;
import org.bukkit.Bukkit;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"lib.homies.framework.command.annotations.Command"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class CommandProcessor extends AbstractProcessor {

    private Platform detectedPlatform = Platform.UNKNOWN;
    private TypeName HOMIES_PLAYER_WRAPPER_TYPE;
    private TypeName HOMIES_LIB_PLATFORM_CLASS;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        if (processingEnv.getElementUtils().getTypeElement("io.papermc.paper.threadedregions.RegionizedServer") != null) {
            this.detectedPlatform = Platform.FOLIA;
            this.HOMIES_PLAYER_WRAPPER_TYPE = ClassName.get("lib.homies.framework.folia.player", "FoliaPlayer");
            this.HOMIES_LIB_PLATFORM_CLASS = ClassName.get("lib.homies.framework.folia", "HomiesLibFolia");
        } else if (processingEnv.getElementUtils().getTypeElement("org.bukkit.Bukkit") != null) {
            this.detectedPlatform = Platform.SPIGOT;
            this.HOMIES_PLAYER_WRAPPER_TYPE = ClassName.get("lib.homies.framework.spigot.player", "SpigotPlayer");
            this.HOMIES_LIB_PLATFORM_CLASS = ClassName.get("lib.homies.framework.spigot", "HomiesLibSpigot");
        } else {
            this.detectedPlatform = Platform.UNKNOWN;
        }
    }

    private final TypeName COMMAND_SENDER_TYPE = ClassName.get("org.bukkit.command", "CommandSender");
    private final TypeName PLAYER_TYPE = ClassName.get("org.bukkit.entity", "Player");
    private final TypeName CHAT_COLOR_TYPE = ClassName.get("org.bukkit", "ChatColor");
    private final TypeName HOMIES_PLAYER_TYPE = ClassName.get("lib.homies.framework.player", "HomiesPlayer");
    private final TypeName SUBCOMMAND_INFO_TYPE = ClassName.get(SubcommandInfo.class);
    private final TypeName TAB_COMPLETER_TYPE = ClassName.get("org.bukkit.command", "TabCompleter");
    private final TypeName LIST_STRING_TYPE = ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(String.class));

    private final Set<TypeElement> discoveredCommandClasses = new LinkedHashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Command.class)) {
            if (element.getKind() != ElementKind.CLASS) continue;

            TypeElement typeElement = (TypeElement) element;
            if (typeElement.getAnnotation(Command.class).name().isEmpty()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Command must have a non-empty name.", typeElement);
                continue;
            }

            discoveredCommandClasses.add(typeElement);

            try {
                generateCommandDispatcher(typeElement);
                generateMetadataClass(typeElement);
                generateTabCompleter(typeElement);
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not generate command files for " + typeElement.getSimpleName() + ": " + e.getMessage(), element);
            }
        }

        if (roundEnv.processingOver()) {
            try {
                generateCommandRegistry();
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not generate command registry: " + e.getMessage());
            }
        }

        return true;
    }

    private void generateCommandRegistry() throws IOException {
        if (discoveredCommandClasses.isEmpty() || detectedPlatform == Platform.UNKNOWN) return;

        String packageName = detectedPlatform.getPackageName() + ".command";
        List<CodeBlock> classNames = discoveredCommandClasses.stream()
                .map(te -> CodeBlock.of("$S", te.getQualifiedName().toString()))
                .collect(Collectors.toList());

        FieldSpec commandClassesField = FieldSpec.builder(LIST_STRING_TYPE, "COMMAND_CLASS_NAMES", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(CodeBlock.builder().add("$T.of(\n    ", List.class).add(CodeBlock.join(classNames, ",\n    ")).add("\n)").build())
                .build();

        TypeSpec registryClass = TypeSpec.classBuilder("HomiesCommandRegistry")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(commandClassesField)
                .build();

        JavaFile.builder(packageName, registryClass).build().writeTo(processingEnv.getFiler());
    }

    private void generateCommandDispatcher(TypeElement commandClass) throws IOException {
        String originalClassName = commandClass.getSimpleName().toString();
        String packageName = processingEnv.getElementUtils().getPackageOf(commandClass).getQualifiedName().toString();
        String generatedClassName = originalClassName + "_GeneratedDispatcher";

        MethodSpec.Builder onCommandMethodBuilder = MethodSpec.methodBuilder("onCommand")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(boolean.class)
                .addParameter(COMMAND_SENDER_TYPE, "sender")
                .addParameter(ClassName.get("org.bukkit.command", "Command"), "command")
                .addParameter(String.class, "label")
                .addParameter(String[].class, "args");

        onCommandMethodBuilder.addStatement("$T plugin = $T.getPlugin($T.class)", ClassName.get("org.bukkit.plugin.java", "JavaPlugin"), ClassName.get("org.bukkit.plugin.java", "JavaPlugin"), HOMIES_LIB_PLATFORM_CLASS);

        Command commandAnnotation = commandClass.getAnnotation(Command.class);
        String basePermission = commandAnnotation.permission();
        if (basePermission != null && !basePermission.isEmpty()) {
            onCommandMethodBuilder.beginControlFlow("if (!sender.hasPermission($S))", basePermission)
                    .addStatement("sender.sendMessage($T.translateAlternateColorCodes('&', $S))", CHAT_COLOR_TYPE, commandAnnotation.permissionMessage())
                    .addStatement("return true")
                    .endControlFlow();
        }

        ExecutableElement defaultCommandMethod = commandClass.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(e -> (ExecutableElement) e)
                .filter(e -> (e.getAnnotation(SubCommand.class) != null && e.getAnnotation(SubCommand.class).value().isEmpty()) || (e.getAnnotation(Command.class) != null && e.getAnnotation(SubCommand.class) == null))
                .findFirst()
                .orElse(null);

        onCommandMethodBuilder.beginControlFlow("if (args.length == 0)");
        if (defaultCommandMethod != null) {
            handleDefaultCommand(onCommandMethodBuilder, defaultCommandMethod);
        } else {
            sendUsageMessage(onCommandMethodBuilder, commandClass.getAnnotation(Command.class).usage(), "&cThis command requires a subcommand. Use /<command> help for available commands.", null);
        }
        onCommandMethodBuilder.addStatement("return true").endControlFlow();

        List<ExecutableElement> subCommandMethods = new ArrayList<>();
        for (Element e : commandClass.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD && e.getAnnotation(SubCommand.class) != null && !e.getAnnotation(SubCommand.class).value().isEmpty()) {
                subCommandMethods.add((ExecutableElement) e);
            }
        }

        if (!subCommandMethods.isEmpty()) {
            Map<Boolean, List<ExecutableElement>> partitionedCommands = subCommandMethods.stream()
                    .collect(Collectors.partitioningBy(e -> getBasePath(e.getAnnotation(SubCommand.class).value()).isEmpty()));

            List<ExecutableElement> placeholderOnlyMethods = partitionedCommands.get(true);
            List<ExecutableElement> namedMethods = partitionedCommands.get(false);

            boolean hasGeneratedNamedCommands = false;
            if (!namedMethods.isEmpty()) {
                Map<String, List<ExecutableElement>> commandsByPath = namedMethods.stream()
                        .collect(Collectors.groupingBy(e -> getBasePath(e.getAnnotation(SubCommand.class).value())));

                List<String> sortedPaths = commandsByPath.keySet().stream()
                        .sorted(Comparator.comparingInt((String p) -> p.split(" ").length).reversed())
                        .collect(Collectors.toList());

                boolean firstIf = true;
                for (String path : sortedPaths) {
                    List<ExecutableElement> overloads = commandsByPath.get(path);
                    String[] pathWords = path.split(" ");
                    CodeBlock commandPathCondition = buildCommandPathCondition(pathWords, overloads.get(0).getAnnotation(SubCommand.class).aliases());
                    if (commandPathCondition.isEmpty()) continue;

                    onCommandMethodBuilder.beginControlFlow("$L ($L)", firstIf ? "if" : "else if", commandPathCondition);
                    firstIf = false;
                    handleSubcommandOverloads(onCommandMethodBuilder, overloads, pathWords.length);
                    onCommandMethodBuilder.endControlFlow();
                }
                hasGeneratedNamedCommands = !firstIf;
            }

            if (!placeholderOnlyMethods.isEmpty()) {
                if (hasGeneratedNamedCommands) onCommandMethodBuilder.beginControlFlow("else");
                handleSubcommandOverloads(onCommandMethodBuilder, placeholderOnlyMethods, 0);
                if (hasGeneratedNamedCommands) onCommandMethodBuilder.endControlFlow();
            } else if (hasGeneratedNamedCommands) {
                onCommandMethodBuilder.beginControlFlow("else");
                sendUsageMessage(onCommandMethodBuilder, commandClass.getAnnotation(Command.class).usage(), "&cUnknown subcommand. Use /<command> help for available commands.", null);
                onCommandMethodBuilder.endControlFlow();
            }
        }

        onCommandMethodBuilder.addStatement("return true");

        TypeSpec generatedClass = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get("org.bukkit.command", "CommandExecutor"))
                .addField(ClassName.get(packageName, originalClassName), "commandInstance", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).addParameter(ClassName.get(packageName, originalClassName), "instance").addStatement("this.commandInstance = instance").build())
                .addMethod(onCommandMethodBuilder.build())
                .build();

        JavaFile.builder(packageName, generatedClass).build().writeTo(processingEnv.getFiler());
    }

    private void generateTabCompleter(TypeElement commandClass) throws IOException {
        List<ExecutableElement> allTabCompleters = new ArrayList<>();
        for (Element e : commandClass.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD && e.getAnnotation(TabComplete.class) != null) {
                allTabCompleters.add((ExecutableElement) e);
            }
        }

        List<ExecutableElement> subCommandMethods = new ArrayList<>();
        for (Element e : commandClass.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD && e.getAnnotation(SubCommand.class) != null && !e.getAnnotation(SubCommand.class).value().isEmpty()) {
                subCommandMethods.add((ExecutableElement) e);
            }
        }

        if (allTabCompleters.isEmpty() && subCommandMethods.isEmpty()) {
            return; // No tab completion to generate
        }

        MethodSpec.Builder onTabCompleteMethodBuilder = MethodSpec.methodBuilder("onTabComplete")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(LIST_STRING_TYPE)
                .addParameter(COMMAND_SENDER_TYPE, "sender")
                .addParameter(ClassName.get("org.bukkit.command", "Command"), "command")
                .addParameter(String.class, "alias")
                .addParameter(String[].class, "args");

        onTabCompleteMethodBuilder.addStatement("final String currentArg = args.length == 0 ? \"\" : args[args.length - 1].toLowerCase()");

        // Automatic permission-based subcommand completion for the first argument
        onTabCompleteMethodBuilder.beginControlFlow("if (args.length == 1)");
        onTabCompleteMethodBuilder.addStatement("$T<String> suggestions = new $T<>()", List.class, ArrayList.class);

        for (ExecutableElement subMethod : subCommandMethods) {
            SubCommand subCmd = subMethod.getAnnotation(SubCommand.class);
            String permission = subCmd.permission();
            String subCommandName = getBasePath(subCmd.value()).split(" ")[0];

            CodeBlock.Builder suggestionBlock = CodeBlock.builder();
            if (permission != null && !permission.isEmpty()) {
                suggestionBlock.beginControlFlow("if (sender.hasPermission($S))", permission);
            }

            suggestionBlock.addStatement("suggestions.add($S)", subCommandName);
            for (String subAlias : subCmd.aliases()) {
                suggestionBlock.addStatement("suggestions.add($S)", subAlias);
            }

            if (permission != null && !permission.isEmpty()) {
                suggestionBlock.endControlFlow();
            }
            onTabCompleteMethodBuilder.addCode(suggestionBlock.build());
        }

        onTabCompleteMethodBuilder.addStatement("return suggestions.stream().filter(s -> s.toLowerCase().startsWith(currentArg)).collect($T.toList())", Collectors.class);
        onTabCompleteMethodBuilder.endControlFlow();

        // Handle user-defined tab completers for deeper arguments
        List<ExecutableElement> otherTabCompleters = allTabCompleters.stream()
                .filter(m -> !m.getAnnotation(TabComplete.class).value().isEmpty())
                .sorted(Comparator.comparingInt((ExecutableElement o) -> o.getAnnotation(TabComplete.class).value().split(" ").length).reversed())
                .collect(Collectors.toList());

        for (ExecutableElement method : otherTabCompleters) {
            TabComplete tabCompleteAnnotation = method.getAnnotation(TabComplete.class);
            String[] tabPathWords = tabCompleteAnnotation.value().split(" ");
            CodeBlock pathCondition = buildTabCompletePathCondition(tabPathWords, tabCompleteAnnotation.aliases());

            onTabCompleteMethodBuilder.beginControlFlow("if ($L)", pathCondition);
            executeTabCompleteMethod(onTabCompleteMethodBuilder, method);
            onTabCompleteMethodBuilder.endControlFlow();
        }

        onTabCompleteMethodBuilder.addStatement("return $T.emptyList()", Collections.class);

        String originalClassName = commandClass.getSimpleName().toString();
        String packageName = processingEnv.getElementUtils().getPackageOf(commandClass).getQualifiedName().toString();
        String generatedClassName = originalClassName + "_GeneratedTabCompleter";

        TypeSpec generatedClass = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(TAB_COMPLETER_TYPE)
                .addField(ClassName.get(packageName, originalClassName), "commandInstance", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).addParameter(ClassName.get(packageName, originalClassName), "instance").addStatement("this.commandInstance = instance").build())
                .addMethod(onTabCompleteMethodBuilder.build())
                .build();

        JavaFile.builder(packageName, generatedClass).build().writeTo(processingEnv.getFiler());
    }

    private void executeTabCompleteMethod(MethodSpec.Builder builder, ExecutableElement method) {
        List<CodeBlock> parameterInvocations = new ArrayList<>();
        for (VariableElement param : method.getParameters()) {
            TypeName paramType = TypeName.get(param.asType());
            if (paramType.equals(COMMAND_SENDER_TYPE)) {
                parameterInvocations.add(CodeBlock.of("sender"));
            } else if (paramType.equals(HOMIES_PLAYER_TYPE)) {
                builder.beginControlFlow("if (!(sender instanceof $T))", PLAYER_TYPE).addStatement("return $T.emptyList()", Collections.class).endControlFlow();
                parameterInvocations.add(CodeBlock.of("new $T(($T) sender)", HOMIES_PLAYER_WRAPPER_TYPE, PLAYER_TYPE));
            } else if (paramType.equals(TypeName.get(String[].class))) {
                parameterInvocations.add(CodeBlock.of("args"));
            } else if (paramType.equals(ClassName.get(String.class))) {
                parameterInvocations.add(CodeBlock.of("currentArg"));
            } else {
                return; // Skip on unsupported param
            }
        }
        builder.addStatement("List<String> suggestions = commandInstance.$L($L)", method.getSimpleName(), CodeBlock.join(parameterInvocations, ", "));
        builder.addStatement("return suggestions.stream().filter(s -> s.toLowerCase().startsWith(currentArg)).collect($T.toList())", Collectors.class);
    }

    private CodeBlock buildCommandPathCondition(String[] pathWords, String[] aliases) {
        List<String> allPaths = new ArrayList<>(Arrays.asList(aliases));
        allPaths.add(String.join(" ", pathWords));

        return allPaths.stream()
                .filter(p -> p != null && !p.isEmpty())
                .map(path -> {
                    String[] words = path.split(" ");
                    CodeBlock.Builder pathCondition = CodeBlock.builder().add("(args.length >= $L", words.length);
                    for (int i = 0; i < words.length; i++) {
                        if (!words[i].startsWith("<")) {
                            pathCondition.add(" && args[$L].equalsIgnoreCase($S)", i, words[i]);
                        }
                    }
                    return pathCondition.add(")").build();
                })
                .collect(CodeBlock.joining(" || "));
    }

    private CodeBlock buildTabCompletePathCondition(String[] pathWords, String[] aliases) {
        List<String> allPaths = new ArrayList<>(Arrays.asList(aliases));
        allPaths.add(String.join(" ", pathWords));

        return allPaths.stream()
                .filter(Objects::nonNull)
                .map(path -> {
                    String[] words = path.isEmpty() ? new String[0] : path.split(" ");
                    CodeBlock.Builder pathCondition = CodeBlock.builder().add("(args.length == $L", words.length + 1);
                    for (int i = 0; i < words.length; i++) {
                        if (!words[i].startsWith("<")) {
                            pathCondition.add(" && args[$L].equalsIgnoreCase($S)", i, words[i]);
                        }
                    }
                    return pathCondition.add(")").build();
                })
                .collect(CodeBlock.joining(" || "));
    }

    private void handleDefaultCommand(MethodSpec.Builder builder, ExecutableElement defaultCommandMethod) {
        String playerOnlyMessage = "";
        boolean isPlayerOnly = false;

        SubCommand subCmdAnnotation = defaultCommandMethod.getAnnotation(SubCommand.class);
        if (subCmdAnnotation != null) {
            isPlayerOnly = subCmdAnnotation.playerOnly();
            playerOnlyMessage = subCmdAnnotation.playerOnlyMessage();
        } else {
            Command cmdAnnotation = defaultCommandMethod.getAnnotation(Command.class);
            if (cmdAnnotation != null) {
                isPlayerOnly = cmdAnnotation.playerOnly();
                playerOnlyMessage = cmdAnnotation.playerOnlyMessage();
            }
        }

        if (isPlayerOnly) {
            builder.beginControlFlow("if (!(sender instanceof $T))", PLAYER_TYPE)
                    .addStatement("sender.sendMessage($T.translateAlternateColorCodes('&', $S))", CHAT_COLOR_TYPE, playerOnlyMessage)
                    .addStatement("return true")
                    .endControlFlow();
        }
        executeCommandMethod(builder, defaultCommandMethod, "commandInstance", 0);
    }

    private void handleSubcommandOverloads(MethodSpec.Builder builder, List<ExecutableElement> overloads, int pathWordCount) {
        overloads.sort(Comparator.comparingInt((ExecutableElement o) -> countPlaceholders(o.getAnnotation(SubCommand.class).value())).reversed());

        boolean firstInnerIf = true;
        for (ExecutableElement method : overloads) {
            SubCommand subCmdAnnotation = method.getAnnotation(SubCommand.class);
            int requiredArgs = countPlaceholders(subCmdAnnotation.value());

            builder.beginControlFlow("$L (args.length - $L == $L)", firstInnerIf ? "if" : "else if", pathWordCount, requiredArgs);
            firstInnerIf = false;

            if (subCmdAnnotation.playerOnly()) {
                builder.beginControlFlow("if (!(sender instanceof $T))", PLAYER_TYPE)
                        .addStatement("sender.sendMessage($T.translateAlternateColorCodes('&', $S))", CHAT_COLOR_TYPE, subCmdAnnotation.playerOnlyMessage())
                        .addStatement("return true")
                        .endControlFlow();
            }

            executeCommandMethod(builder, method, "commandInstance", pathWordCount);
            builder.addStatement("return true").endControlFlow();
        }

        builder.beginControlFlow("else");
        sendUsageMessage(builder, overloads.get(0).getAnnotation(SubCommand.class).usage(), "&cInvalid arguments for command.", getBasePath(overloads.get(0).getAnnotation(SubCommand.class).value()));
        builder.endControlFlow();
    }

    private void executeCommandMethod(MethodSpec.Builder builder, ExecutableElement method, String instanceName, int argStartIndex) {
        builder.beginControlFlow("try");

        String permission = "";
        String permissionMessage = "";
        SubCommand subCmd = method.getAnnotation(SubCommand.class);
        if (subCmd != null) {
            permission = subCmd.permission();
            permissionMessage = subCmd.permissionMessage();
        } else {
            Command cmd = method.getAnnotation(Command.class);
            if (cmd != null) {
                permission = cmd.permission();
                permissionMessage = cmd.permissionMessage();
            }
        }

        if (permission != null && !permission.isEmpty()) {
            builder.beginControlFlow("if (!sender.hasPermission($S))", permission)
                    .addStatement("sender.sendMessage($T.translateAlternateColorCodes('&', $S))", CHAT_COLOR_TYPE, permissionMessage)
                    .addStatement("return true")
                    .endControlFlow();
        }

        List<CodeBlock> parameterInvocations = new ArrayList<>();
        int currentArgIndex = argStartIndex;
        boolean senderInjected = false;

        for (VariableElement param : method.getParameters()) {
            TypeName paramType = TypeName.get(param.asType());

            if (paramType.equals(HOMIES_PLAYER_TYPE) && !senderInjected) {
                builder.beginControlFlow("if (!(sender instanceof $T))", PLAYER_TYPE)
                        .addStatement("sender.sendMessage($T.translateAlternateColorCodes('&', $S))", CHAT_COLOR_TYPE, "This command can only be run by a player.")
                        .addStatement("return true")
                        .endControlFlow();
                parameterInvocations.add(CodeBlock.of("new $T(($T) sender)", HOMIES_PLAYER_WRAPPER_TYPE, PLAYER_TYPE));
                senderInjected = true;
            } else if (paramType.equals(COMMAND_SENDER_TYPE) && !senderInjected) {
                parameterInvocations.add(CodeBlock.of("sender"));
                senderInjected = true;
            } else {
                String paramName = param.getSimpleName().toString() + "_" + currentArgIndex;
                if (paramType.equals(HOMIES_PLAYER_TYPE)) {
                    builder.addStatement("$T $L = $T.getPlayer(args[$L])", PLAYER_TYPE, paramName, Bukkit.class, currentArgIndex);
                    builder.beginControlFlow("if ($L == null)", paramName)
                            .addStatement("sender.sendMessage($T.translateAlternateColorCodes('&', \"&cPlayer not found: \" + args[$L]))", CHAT_COLOR_TYPE, currentArgIndex)
                            .addStatement("return true")
                            .endControlFlow();
                    parameterInvocations.add(CodeBlock.of("new $T($L)", HOMIES_PLAYER_WRAPPER_TYPE, paramName));
                    currentArgIndex++;
                } else if (paramType.equals(ClassName.get(String.class))) {
                    parameterInvocations.add(CodeBlock.of("args[$L]", currentArgIndex));
                    currentArgIndex++;
                } else if (paramType.equals(TypeName.INT)) {
                    builder.addStatement("int $L = 0", paramName);
                    builder.beginControlFlow("try")
                            .addStatement("$L = $T.parseInt(args[$L])", paramName, Integer.class, currentArgIndex)
                            .nextControlFlow("catch ($T e)", NumberFormatException.class)
                            .addStatement("sender.sendMessage($T.translateAlternateColorCodes('&', \"&cInvalid number: \" + args[$L]))", CHAT_COLOR_TYPE, currentArgIndex)
                            .addStatement("return true")
                            .endControlFlow();
                    parameterInvocations.add(CodeBlock.of("$L", paramName));
                    currentArgIndex++;
                } else {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported parameter type: " + paramType + " for command method.", param);
                    builder.addStatement("return true");
                    return;
                }
            }
        }

        CodeBlock methodCall = CodeBlock.builder().add("$L.$L($L)", instanceName, method.getSimpleName(), CodeBlock.join(parameterInvocations, ", ")).build();

        if (method.getAnnotation(Async.class) != null) {
            if (detectedPlatform == Platform.FOLIA) {
                builder.addStatement("$T.getGlobalRegionScheduler().run(plugin, () -> $L)", Bukkit.class, methodCall);
            } else {
                builder.addStatement("$T.getScheduler().runTaskAsynchronously(plugin, () -> $L)", Bukkit.class, methodCall);
            }
        } else {
            builder.addStatement(methodCall);
        }

        builder.nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("sender.sendMessage($T.translateAlternateColorCodes('&', $S))", CHAT_COLOR_TYPE, "&cAn unexpected internal error occurred.")
                .addStatement("e.printStackTrace()");
        builder.endControlFlow();
    }

    private int countPlaceholders(String value) {
        Matcher matcher = Pattern.compile("<[^>]+>").matcher(value);
        int count = 0;
        while (matcher.find()) count++;
        return count;
    }

    private void sendUsageMessage(MethodSpec.Builder builder, String usage, String defaultMessage, String subCommand) {
        String message = usage.isEmpty() ? defaultMessage : usage;
        if (subCommand != null) {
            builder.addStatement("sender.sendMessage($T.translateAlternateColorCodes('&', $S).replace(\"<command>\", label).replace(\"<subcommand>\", $S))",
                    CHAT_COLOR_TYPE, message, subCommand);
        } else {
            builder.addStatement("sender.sendMessage($T.translateAlternateColorCodes('&', $S).replace(\"<command>\", label))",
                    CHAT_COLOR_TYPE, message);
        }
    }

    private String getBasePath(String value) {
        return value.replaceAll("<[^>]+>", "").trim().replaceAll("\\s+", " ");
    }

    private void generateMetadataClass(TypeElement commandClass) throws IOException {
        String originalClassName = commandClass.getSimpleName().toString();
        String packageName = processingEnv.getElementUtils().getPackageOf(commandClass).getQualifiedName().toString();
        String generatedClassName = originalClassName + "_Metadata";

        ParameterizedTypeName listOfSubcommands = ParameterizedTypeName.get(ClassName.get(List.class), SUBCOMMAND_INFO_TYPE);
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(listOfSubcommands, "SUBCOMMANDS", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        List<ExecutableElement> subCommandMethods = new ArrayList<>();
        for (Element e : commandClass.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD && e.getAnnotation(SubCommand.class) != null) {
                subCommandMethods.add((ExecutableElement) e);
            }
        }

        CodeBlock.Builder listInitializer = CodeBlock.builder();
        if (subCommandMethods.isEmpty()) {
            listInitializer.add("$T.of()", List.class);
        } else {
            listInitializer.add("$T.of(\n", List.class);
            for (int i = 0; i < subCommandMethods.size(); i++) {
                ExecutableElement methodElement = subCommandMethods.get(i);
                SubCommand subCommandAnnotation = methodElement.getAnnotation(SubCommand.class);
                String permission = subCommandAnnotation.permission();

                List<String> paramTypes = methodElement.getParameters().stream().map(param -> param.asType().toString()).toList();
                CodeBlock paramList = paramTypes.isEmpty()
                        ? CodeBlock.of("$T.of()", List.class)
                        : CodeBlock.of("$T.of($L)", List.class, paramTypes.stream().map(p -> CodeBlock.of("$S", p)).collect(CodeBlock.joining(", ")));

                listInitializer.add("    new $T($S, $S, $S, $L, $L, $S)",
                        SUBCOMMAND_INFO_TYPE,
                        subCommandAnnotation.value(),
                        subCommandAnnotation.description(),
                        subCommandAnnotation.usage(),
                        methodElement.getParameters().size(),
                        paramList,
                        permission != null ? permission : "");

                if (i < subCommandMethods.size() - 1) listInitializer.add(",\n");
            }
            listInitializer.add("\n)");
        }

        fieldBuilder.initializer(listInitializer.build());

        TypeSpec metadataClass = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(fieldBuilder.build())
                .build();

        JavaFile.builder(packageName, metadataClass).build().writeTo(processingEnv.getFiler());
    }

    private enum Platform {
        SPIGOT, FOLIA, UNKNOWN;

        public String getPackageName() {
            return this == SPIGOT ? "lib.homies.framework.spigot" : "lib.homies.framework.folia";
        }
    }
}
