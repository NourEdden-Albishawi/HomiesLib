package processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import lib.homies.framework.command.annotations.Async;
import lib.homies.framework.command.annotations.Command;
import lib.homies.framework.command.annotations.Permission;
import lib.homies.framework.command.annotations.SubCommand;
import lib.homies.framework.command.context.SubcommandInfo;
import org.bukkit.Bukkit;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes("lib.homies.framework.command.annotations.Command")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class CommandProcessor extends AbstractProcessor {

    private final TypeName COMMAND_SENDER_TYPE = ClassName.get("org.bukkit.command", "CommandSender");
    private final TypeName PLAYER_TYPE = ClassName.get("org.bukkit.entity", "Player");
    private final TypeName HOMIES_PLAYER_TYPE = ClassName.get("lib.homies.framework.player", "HomiesPlayer");
    private final TypeName SPIGOT_PLAYER_TYPE = ClassName.get("lib.homies.framework.spigot.player", "SpigotPlayer");
    private final TypeName SUBCOMMAND_INFO_TYPE = ClassName.get(SubcommandInfo.class);

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Command.class)) {
            if (element.getKind() != ElementKind.CLASS) continue;

            TypeElement typeElement = (TypeElement) element;
            try {
                generateCommandDispatcher(typeElement);
                generateMetadataClass(typeElement);
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "Could not generate command dispatcher: " + e.getMessage(), element);
            }
        }
        return true;
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

        ClassName homiesLibSpigotClass = ClassName.get("lib.homies.framework.spigot", "HomiesLibSpigot");
        onCommandMethodBuilder.addStatement("$T plugin = $T.getPlugin($T.class)", ClassName.get("org.bukkit.plugin.java", "JavaPlugin"), ClassName.get("org.bukkit.plugin.java", "JavaPlugin"), homiesLibSpigotClass);

        ExecutableElement defaultCommandMethod = commandClass.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD && e.getAnnotation(Command.class) != null)
                .map(e -> (ExecutableElement) e)
                .findFirst()
                .orElse(null);

        onCommandMethodBuilder.beginControlFlow("if (args.length == 0)");
        if (defaultCommandMethod != null) {
            onCommandMethodBuilder.beginControlFlow("try");
            executeCommandMethod(onCommandMethodBuilder, defaultCommandMethod, "commandInstance", true);
            onCommandMethodBuilder.nextControlFlow("catch ($T e)", Exception.class)
                    .addStatement("sender.sendMessage(\"§cAn unexpected internal error occurred.\")")
                    .addStatement("e.printStackTrace()");
            onCommandMethodBuilder.endControlFlow();
        } else {
            Command commandAnnotation = commandClass.getAnnotation(Command.class);
            String usage = commandAnnotation.usage();
            if (usage.isEmpty()) {
                usage = "§cThis command requires a subcommand. Use /" + "label" + " help for available commands.";
            }
            onCommandMethodBuilder.addStatement("sender.sendMessage($S)", usage);
        }
        onCommandMethodBuilder.addStatement("return true");
        onCommandMethodBuilder.endControlFlow();

        onCommandMethodBuilder.addStatement("String subCommandName = args[0].toLowerCase()");
        onCommandMethodBuilder.beginControlFlow("switch (subCommandName)");

        Map<String, List<ExecutableElement>> subCommandsByName = commandClass.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD && e.getAnnotation(SubCommand.class) != null)
                .map(e -> (ExecutableElement) e)
                .collect(Collectors.groupingBy(e -> e.getAnnotation(SubCommand.class).value().split(" ")[0].toLowerCase()));

        for (Map.Entry<String, List<ExecutableElement>> entry : subCommandsByName.entrySet()) {
            String subCommandName = entry.getKey();
            List<ExecutableElement> overloads = entry.getValue();

            onCommandMethodBuilder.beginControlFlow("case $S:", subCommandName);
            onCommandMethodBuilder.beginControlFlow("try");

            overloads.sort(Comparator.comparingInt(o -> o.getParameters().size()));

            for (int i = 0; i < overloads.size(); i++) {
                ExecutableElement methodElement = overloads.get(i);
                int requiredArgs = (int) methodElement.getParameters().stream().filter(p -> !TypeName.get(p.asType()).equals(HOMIES_PLAYER_TYPE) || p.getAnnotation(SubCommand.class) != null).count();

                String controlFlow = (i == 0) ? "if" : "else if";
                onCommandMethodBuilder.beginControlFlow("$L (args.length - 1 == $L)", controlFlow, requiredArgs);

                executeCommandMethod(onCommandMethodBuilder, methodElement, "commandInstance", false);

                onCommandMethodBuilder.addStatement("return true");
                onCommandMethodBuilder.endControlFlow();
            }

            String usageMessage = overloads.get(0).getAnnotation(SubCommand.class).usage();
            if (usageMessage.isEmpty()) {
                usageMessage = "Invalid arguments for command /" + "label" + " " + subCommandName;
            }
            onCommandMethodBuilder.beginControlFlow("else")
                    .addStatement("sender.sendMessage($S)", usageMessage)
                    .endControlFlow();

            onCommandMethodBuilder.nextControlFlow("catch ($T e)", Exception.class)
                    .addStatement("sender.sendMessage(\"§cAn unexpected internal error occurred.\")")
                    .addStatement("e.printStackTrace()");
            onCommandMethodBuilder.endControlFlow();

            onCommandMethodBuilder.addStatement("break");
            onCommandMethodBuilder.endControlFlow();
        }

        onCommandMethodBuilder.beginControlFlow("default:");
        Command commandAnnotation = commandClass.getAnnotation(Command.class);
        String usage = commandAnnotation.usage();
        if (usage.isEmpty()) {
            usage = "§cUnknown subcommand. Use /" + "label" + " help for available commands.";
        }
        onCommandMethodBuilder.addStatement("sender.sendMessage($S)", usage);
        onCommandMethodBuilder.addStatement("break");
        onCommandMethodBuilder.endControlFlow();

        onCommandMethodBuilder.endControlFlow();
        onCommandMethodBuilder.addStatement("return true");

        TypeSpec generatedClass = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get("org.bukkit.command", "CommandExecutor"))
                .addField(ClassName.get(packageName, originalClassName), "commandInstance", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.get(packageName, originalClassName), "instance")
                        .addStatement("this.commandInstance = instance")
                        .build())
                .addMethod(onCommandMethodBuilder.build())
                .build();

        JavaFile.builder(packageName, generatedClass).build().writeTo(processingEnv.getFiler());
    }

    private void executeCommandMethod(MethodSpec.Builder builder, ExecutableElement method, String instanceName, boolean isDefaultCommand) {
        Permission perm = method.getAnnotation(Permission.class);
        if (perm != null) {
            builder.beginControlFlow("if (!sender.hasPermission($S))", perm.value())
                    .addStatement("sender.sendMessage($S)", perm.message())
                    .addStatement("return")
                    .endControlFlow();
        }

        List<CodeBlock> parameterInvocations = new ArrayList<>();
        int stringArgIndex = isDefaultCommand ? 0 : 1;
        boolean senderInjected = false;

        for (VariableElement param : method.getParameters()) {
            TypeName paramType = TypeName.get(param.asType());
            String paramName = param.getSimpleName().toString() + "_" + stringArgIndex;

            if (paramType.equals(HOMIES_PLAYER_TYPE) && !senderInjected) {
                builder.beginControlFlow("if (!(sender instanceof $T))", PLAYER_TYPE)
                        .addStatement("sender.sendMessage(\"This command must be run by a player.\")")
                        .addStatement("return")
                        .endControlFlow();
                parameterInvocations.add(CodeBlock.of("new $T(($T) sender)", SPIGOT_PLAYER_TYPE, PLAYER_TYPE));
                senderInjected = true;
            } else {
                if (isDefaultCommand) {
                    processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "Default command methods can only accept an injectable sender parameter (e.g., HomiesPlayer).", param);
                    builder.addStatement("sender.sendMessage(\"§cError: The default command is configured incorrectly.\")");
                    builder.addStatement("return");
                    return;
                }

                if (paramType.equals(HOMIES_PLAYER_TYPE)) {
                    builder.addStatement("$T $L = $T.getPlayer(args[$L])", PLAYER_TYPE, paramName, Bukkit.class, stringArgIndex);
                    builder.beginControlFlow("if ($L == null)", paramName)
                            .addStatement("sender.sendMessage(\"Player not found: \" + args[$L])", stringArgIndex)
                            .addStatement("return")
                            .endControlFlow();
                    parameterInvocations.add(CodeBlock.of("new $T($L)", SPIGOT_PLAYER_TYPE, paramName));
                    stringArgIndex++;
                } else if (paramType.equals(ClassName.get(String.class))) {
                    parameterInvocations.add(CodeBlock.of("args[$L]", stringArgIndex));
                    stringArgIndex++;
                } else if (paramType.equals(TypeName.INT)) {
                    builder.addStatement("int $L = 0", paramName);
                    builder.beginControlFlow("try")
                            .addStatement("$L = $T.parseInt(args[$L])", paramName, Integer.class, stringArgIndex)
                            .nextControlFlow("catch ($T e)", NumberFormatException.class)
                            .addStatement("sender.sendMessage(\"Invalid number: \" + args[$L])", stringArgIndex)
                            .addStatement("return")
                            .endControlFlow();
                    parameterInvocations.add(CodeBlock.of("$L", paramName));
                    stringArgIndex++;
                } else {
                    processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "Unsupported parameter type: " + paramType, param);
                    builder.addStatement("return");
                    break;
                }
            }
        }

        CodeBlock methodCall = CodeBlock.builder()
                .add("$L.$L(", instanceName, method.getSimpleName())
                .add(CodeBlock.join(parameterInvocations, ", "))
                .add(")")
                .build();

        if (method.getAnnotation(Async.class) != null) {
            builder.addStatement("$T.getScheduler().runTaskAsynchronously(plugin, () -> $L)", Bukkit.class, methodCall);
        } else {
            builder.addStatement("$L", methodCall);
        }
    }


    private void generateMetadataClass(TypeElement commandClass) throws IOException {
        String originalClassName = commandClass.getSimpleName().toString();
        String packageName = processingEnv.getElementUtils().getPackageOf(commandClass).getQualifiedName().toString();
        String generatedClassName = originalClassName + "_Metadata";

        ParameterizedTypeName listOfSubcommands = ParameterizedTypeName.get(ClassName.get(List.class), SUBCOMMAND_INFO_TYPE);

        FieldSpec.Builder fieldBuilder = FieldSpec.builder(listOfSubcommands, "SUBCOMMANDS", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        List<ExecutableElement> subCommandMethods = commandClass.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD && e.getAnnotation(SubCommand.class) != null)
                .map(e -> (ExecutableElement) e)
                .toList();

        CodeBlock.Builder listInitializer = CodeBlock.builder();
        if (subCommandMethods.isEmpty()) {
            listInitializer.add("$T.of()", List.class);
        } else {
            listInitializer.add("$T.of(\n", List.class);
            for (int i = 0; i < subCommandMethods.size(); i++) {
                ExecutableElement methodElement = subCommandMethods.get(i);
                SubCommand subCommandAnnotation = methodElement.getAnnotation(SubCommand.class);
                Permission permissionAnnotation = methodElement.getAnnotation(Permission.class);

                List<String> paramTypes = methodElement.getParameters().stream()
                        .map(param -> param.asType().toString())
                        .toList();

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
                        permissionAnnotation != null ? permissionAnnotation.value() : "");

                if (i < subCommandMethods.size() - 1) {
                    listInitializer.add(",\n");
                }
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
}
