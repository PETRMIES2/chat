package com.sope.utils;

import java.io.File;
import java.util.EnumSet;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.reflections.Reflections;

public class Hibernate5DDLExporter {

    private String dialect = "org.hibernate.dialect.MySQL5InnoDBDialect";
    private String[] entityPackages;

    public Hibernate5DDLExporter dialect(final String dialect) {
        this.dialect = dialect;
        return this;
    }

    public Hibernate5DDLExporter entities(final String... entityPackage) {
        this.entityPackages = entityPackage;
        return this;
    }

    public Hibernate5DDLExporter schemaExport(final String fileName, final String targetDirectory) throws Exception {
        if (entityPackages == null && entityPackages.length == 0) {
            System.out.println("Not packages selected");
            System.exit(0);
        }
        final File exportFile = createExportFileAndMakeDirectory(fileName, targetDirectory);

        final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySetting(AvailableSettings.DIALECT, dialect).build();

        final MetadataImplementor metadata = (MetadataImplementor) mapAnnotatedClasses(serviceRegistry).buildMetadata();

        final SchemaExport schemaExport = new SchemaExport();
        schemaExport.setOutputFile(exportFile.getAbsolutePath());
        schemaExport.setDelimiter(";");
        schemaExport.setFormat(true);
        final EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.SCRIPT);
        schemaExport.execute(targetTypes, SchemaExport.Action.CREATE, metadata);

        //        SchemaUpdate schemaUpdate = new SchemaUpdate();
        //        schemaUpdate.setOutputFile(exportFile.getAbsolutePath());
        //        schemaUpdate.setDelimiter(";");
        //        schemaUpdate.setFormat(true);
        //        EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.SCRIPT);
        //        schemaUpdate.execute(targetTypes, metadata);

        ((StandardServiceRegistryImpl) serviceRegistry).destroy();

        System.out.println(exportFile.getAbsolutePath());

        return this;

    }

    private File createExportFileAndMakeDirectory(final String fileName, final String targetDirectory) {
        File exportFile;
        if (targetDirectory != null) {
            final File directory = new File(targetDirectory);
            directory.mkdirs();
            exportFile = new File(directory, fileName);
        } else {
            exportFile = new File(fileName);
        }
        if (exportFile.exists()) {
            exportFile.delete();
        }
        return exportFile;
    }

    private MetadataSources mapAnnotatedClasses(final ServiceRegistry serviceRegistry) {
        final MetadataSources sources = new MetadataSources(serviceRegistry);

        final Reflections reflections = new Reflections((Object) entityPackages);
        for (final Class<?> mappedSuperClass : reflections.getTypesAnnotatedWith(MappedSuperclass.class)) {
            sources.addAnnotatedClass(mappedSuperClass);
            System.out.println("Mapped = " + mappedSuperClass.getName());
        }
        for (final Class<?> entityClasses : reflections.getTypesAnnotatedWith(Entity.class)) {
            sources.addAnnotatedClass(entityClasses);
            System.out.println("Mapped = " + entityClasses.getName());
        }
        return sources;
    }

    public static Hibernate5DDLExporter instance() {
        return new Hibernate5DDLExporter();
    }

    public static void main(final String[] args) throws Exception {
        Hibernate5DDLExporter.instance().entities("com.sope.domain").schemaExport("db-create.sql", "build");
    }
}