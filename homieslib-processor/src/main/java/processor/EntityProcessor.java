package processor;

import com.google.auto.service.AutoService;
import lib.homies.framework.database.annotations.DbEntity;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"lib.homies.framework.database.annotations.DbEntity"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class EntityProcessor extends AbstractProcessor {

    private final Set<TypeElement> discoveredEntityClasses = new LinkedHashSet<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(DbEntity.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@DbEntity can only be applied to classes.", element);
                continue;
            }
            discoveredEntityClasses.add((TypeElement) element);
        }

        if (roundEnv.processingOver() && !discoveredEntityClasses.isEmpty()) {
            try {
                writeEntityListFile();
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not write entity list file: " + e.getMessage());
            }
        }

        return true;
    }

    private void writeEntityListFile() throws IOException {
        // Use Filer to create a resource file in META-INF/homieslib/
        FileObject fileObject = processingEnv.getFiler().createResource(
                javax.tools.StandardLocation.CLASS_OUTPUT, "", "META-INF/homieslib/entities.list"
        );

        try (Writer writer = fileObject.openWriter()) {
            for (TypeElement entityClass : discoveredEntityClasses) {
                writer.write(entityClass.getQualifiedName().toString());
                writer.write(System.lineSeparator());
            }
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generated META-INF/homieslib/entities.list with " + discoveredEntityClasses.size() + " entities.");
    }
}
