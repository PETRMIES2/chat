package utils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.sope.domain.IdEntity;

import utils.database.SpringContextHolder;

public class EntityGraphPersister {

    /**
     * Persists entity graph
     * 
     * @param source
     *            root of the entity graph
     * @param <EntityType>
     *            type of the entity to be persisted
     * @return persisted entity
     */
    public static <EntityType> EntityType persist(EntityType source) {
        saveDependencies(source);
        return save(source);
    }

    private static <EntityType> EntityType save(EntityType entity) {
        if (entity == null) {
            return null;
        }
        SpringContextHolder.getEntityBuilderSessionFactory().getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    private static <EntityType> void saveDependencies(EntityType entity) {
        Set<Field> traversed = new HashSet<>();
        Set<Field> fields = getAllFields(entity.getClass());

        for (Field field : fields) {
            if (traversed.contains(field)) {
                continue;
            }

            boolean accessible = field.isAccessible();
            field.setAccessible(true);

            try {
                if (IdEntity.class.isAssignableFrom(field.getType())) {
                    saveIdEntity(entity, traversed, field);
                } else if (Iterable.class.isAssignableFrom(field.getType())) {
                    Iterable<?> iterable = (Iterable<?>) field.get(entity);
                    saveIterable(iterable, traversed);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException("Reflection error while saving dependencies", e);
            } finally {
                field.setAccessible(accessible);
            }
        }
    }

    private static <EntityType> void saveIterable(Iterable<EntityType> iterable, Set<Field> traversed) throws IllegalArgumentException, IllegalAccessException {
        if (iterable == null) {
            return;
        }

        for (Object entityInIterable : iterable) {
            if (entityInIterable == null) {
                continue;
            }

            // The item in iterable might be iterable: List<List<IdEntity>>
            if (Iterable.class.isAssignableFrom(entityInIterable.getClass())) {
                saveIterable((Iterable<?>) entityInIterable, traversed);
            } else {
                // The item has not been checked yet to be IdEntity
                if (IdEntity.class.isAssignableFrom(entityInIterable.getClass())) {
                    save((IdEntity) entityInIterable);
                }
            }
        }
    }

    private static <C> void saveIdEntity(C entity, Set<Field> traversed, Field field) throws IllegalArgumentException, IllegalAccessException {
        Object fieldValue = save((IdEntity) field.get(entity));
        field.set(entity, fieldValue);
        traversed.add(field);
    }

    private static <EntityType> Set<Field> getAllFields(Class<EntityType> type) {
        final Set<Field> result = new HashSet<>();

        ReflectionUtils.doWithFields(type, new FieldCallback() {

            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                boolean matches = true;

                if (field.isSynthetic()) {
                    matches = false;// Some frameworks add unnecessary synthetic
                                    // fields
                }

                if (!(IdEntity.class.isAssignableFrom(field.getType()) || Iterable.class.isAssignableFrom(field.getType()))) {
                    matches = false;// Matching only IdEntities or collections
                }
                if (matches) {
                    result.add(field);
                }

            }
        });

        return result;
    }
}
