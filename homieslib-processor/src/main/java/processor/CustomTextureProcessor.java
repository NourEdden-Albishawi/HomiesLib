package processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lib.homies.framework.texture.CustomTexture;
import org.bukkit.Material;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("lib.homies.framework.texture.annotations.CustomTexture")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class CustomTextureProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<Element> textureElements = new ArrayList<>(roundEnv.getElementsAnnotatedWith(CustomTexture.class));
        if (textureElements.isEmpty()) {
            return true;
        }

        String packageName = "lib.homies.framework.spigot.texture";
        String generatedClassName = "CustomTextureRegistry";

        MethodSpec.Builder getTextureMethodBuilder = MethodSpec.methodBuilder("getTexture")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("org.bukkit.inventory", "ItemStack"))
                .addParameter(String.class, "name");

        getTextureMethodBuilder.beginControlFlow("switch (name)");

        for (Element element : textureElements) {
            if (element.getKind() != ElementKind.FIELD) {
                processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "Only fields can be annotated with @CustomTexture", element);
                return true;
            }

            VariableElement variableElement = (VariableElement) element;
            CustomTexture annotation = variableElement.getAnnotation(CustomTexture.class);
            String materialName = annotation.material();
            int value = annotation.value();
            String fieldName = variableElement.getSimpleName().toString();

            try {
                Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                processingEnv.getMessager().printMessage(
                        javax.tools.Diagnostic.Kind.ERROR,
                        "Invalid material name '" + materialName + "' in @CustomTexture for field '" + fieldName + "'.",
                        element
                );
                return true;
            }

            getTextureMethodBuilder.beginControlFlow("case $S:", fieldName)
                    .addStatement("$T itemStack = new $T($T.valueOf($S))",
                            ClassName.get("org.bukkit.inventory", "ItemStack"),
                            ClassName.get("org.bukkit.inventory", "ItemStack"),
                            ClassName.get(Material.class),
                            materialName.toUpperCase())
                    .addStatement("$T itemMeta = itemStack.getItemMeta()", ClassName.get("org.bukkit.inventory.meta", "ItemMeta"))
                    .addStatement("itemMeta.setCustomModelData($L)", value)
                    .addStatement("itemStack.setItemMeta(itemMeta)")
                    .addStatement("return itemStack")
                    .endControlFlow();
        }

        getTextureMethodBuilder.beginControlFlow("default:")
                .addStatement("return null")
                .endControlFlow();

        getTextureMethodBuilder.endControlFlow();

        TypeSpec generatedClass = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(getTextureMethodBuilder.build())
                .build();

        try {
            JavaFile.builder(packageName, generatedClass).build().writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "Could not generate custom texture registry: " + e.getMessage());
        }

        return true;
    }
}
