package com.kvo.utils;

import com.alibaba.fastjson.JSON;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kvo.utils.identity.Exceptions.convertReflectionExceptionToUnchecked;

/**
 * 对象操作类，针对擦除获取和反射获取
 */
@Slf4j
public abstract class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {

    private static final String JAVAP = "java.";
    private static final String JAVADATESTR = "java.util.Date";

    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";
    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    private static Logger logger = LoggerFactory.getLogger(ObjectUtils.class);


    /**
     * 通过JSON序列化进行深复制
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T deepCloneByJSON(T t) {
        return (T) JSON.parseObject(JSON.toJSONString(t), t.getClass());
    }

    /**
     * 调用Getter方法.
     */
    public static Object invokeGetter(Object obj, String propertyName) {
        String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(propertyName);
        return invokeMethod(obj, getterMethodName, new Class[]{}, new Object[]{});
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     */
    public static void invokeSetter(Object obj, String propertyName, Object value) {
        String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(propertyName);
        invokeMethodByName(obj, setterMethodName, new Object[]{value});
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
     * 只匹配函数名，如果有多个同名函数调用第一个。
     */
    public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
        Method method = getAccessibleMethodByName(obj, methodName);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 只匹配函数名。
     * <p>
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }


    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
     * 同时匹配方法名+参数类型，
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 匹配函数名+参数类型。
     * <p>
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName,
                                             final Class<?>... parameterTypes) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getAccessibleField(final Object obj, final String fieldName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(fieldName, "fieldName can't be blank");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {// NOSONAR
                // Field不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处
     * 如无法找到, 返回Object.class.
     * eg.
     * public UserDao extends HibernateDao<User>
     *
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be determined
     */
    public static <T> Class<T> getClassGenricType(final Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     * <p>
     * 如public UserDao extends HibernateDao<User,Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined
     */
    public static Class getClassGenricType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if ((index >= params.length) || (index < 0)) {
            logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class) params[index];
    }

    public static Class<?> getUserClass(Object instance) {
        Validate.notNull(instance, "Instance must not be null");
        Class clazz = instance.getClass();
        if ((clazz != null) && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if ((superClass != null) && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;

    }

    /**
     * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
                .isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 擦除获取泛型类的实际类
     *
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericInterface(Class<?> classOfT) {
        return getGenericInterface(classOfT, Object.class, 0, 0);
    }


    /**
     * 擦除获取泛型类的实际类
     *
     * @param classOfT
     * @param degenerateClass 泛型退化类
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericInterface(Class<?> classOfT, Class<? super T> degenerateClass) {
        return getGenericInterface(classOfT, degenerateClass, 0, 0);
    }


    /**
     * 擦除获取泛型接口
     *
     * @param classOfT
     * @param degenerateClass 泛型退化类
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericInterface(Class<?> classOfT, Class<? super T> degenerateClass, int superIndex, int paramsIndex) {
        Class<?> clazz = classOfT;
        if (!clazz.isInterface()) {
            return null;
        }
        Class<?> clazzResult = classOfT;
        while (!(clazz.getGenericInterfaces()[superIndex] instanceof ParameterizedType) && clazz != degenerateClass) {
            // 子类循环父类，获取最终的接口
            clazz = clazz.getInterfaces()[superIndex];
        }
        try {
            if (clazz != Object.class) {
                Type type = clazz.getGenericInterfaces()[superIndex];
                Type trueType = ((ParameterizedType) type).getActualTypeArguments()[paramsIndex];
                if (trueType instanceof ParameterizedType) {
                    //泛型嵌套，获取具体泛型类
                    return (Class<T>) ((ParameterizedType) trueType).getRawType();
                } else {
                    // 普通类，直接提取对应类
                    return (Class<T>) trueType;
                }
            } else {
                // 泛型退化
                clazzResult = degenerateClass;
            }
        } catch (Exception e) {
            log.error(String.format("getGenericInterface classOfT<%s> degenerateClass<%s> superIndex<%s> paramsIndex", classOfT.getName(), degenerateClass.getName(), superIndex, paramsIndex), e);
            throw new RuntimeException(e);
        }
        return (Class<T>) clazzResult;
    }

    /**
     * 擦除获取泛型类的实际类
     *
     * @param type
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericClass(Type type) {
        return getGenericClass(type, 0);
    }

    /**
     * 擦除获取泛型类的实际类
     *
     * @param type
     * @param paramsIndex
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericClass(Type type, int paramsIndex) {
        Type trueType = ((ParameterizedType) type).getActualTypeArguments()[paramsIndex];
        if (trueType instanceof ParameterizedType) {
            //泛型嵌套，获取具体泛型类
            return (Class<T>) ((ParameterizedType) trueType).getRawType();
        } else {
            // 普通类，直接提取对应类
            return (Class<T>) trueType;
        }
    }

    /**
     * 擦除获取泛型类的实际类
     *
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericClass(Class<?> classOfT) {
        return ObjectUtils.getGenericClass(classOfT, Object.class, 0);
    }


    /**
     * 擦除获取泛型类的实际类
     *
     * @param classOfT
     * @param degenerateClass 泛型退化类
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericClass(Class<?> classOfT, Class<? super T> degenerateClass) {
        return ObjectUtils.getGenericClass(classOfT, degenerateClass, 0);
    }

    /**
     * 擦除获取泛型类
     *
     * @param classOfT
     * @param degenerateClass 泛型退化类
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericClass(Class<?> classOfT, Class<? super T> degenerateClass, int paramsIndex) {
        Class<?> clazz = classOfT;
        if (clazz.isInterface()) {
            return null;
        }
        Class<?> clazzResult = classOfT;
        // 判断当前类
        if (classOfT.getGenericInterfaces().length > 0 && classOfT.getGenericInterfaces().length <= (paramsIndex + 1)) {
            return (Class<T>) ((ParameterizedType) classOfT.getGenericInterfaces()[paramsIndex]).getActualTypeArguments()[paramsIndex];
        }
        // 父类泛型
        while (!(clazz.getGenericSuperclass() instanceof ParameterizedType) && clazz != degenerateClass) {
            // 子类循环父类，获取最终的接口
            clazz = clazz.getSuperclass();
        }
        try {
            if (clazz != Object.class) {
                Type type = clazz.getGenericSuperclass();
                clazzResult = ObjectUtils.<T>getGenericClass(type, paramsIndex);
            } else {
                // 泛型退化
                clazzResult = degenerateClass;
            }
        } catch (Exception e) {
            log.error(String.format("getGenericClass classOfT<%s> degenerateClass<%s> paramsIndex<%s>", classOfT.getName(), degenerateClass.getName(), paramsIndex));
            throw new RuntimeException(e);
        }
        return (Class<T>) clazzResult;
    }

    /**
     * 利用反射获取指定对象的指定属性
     *
     * @param object    目标对象
     * @param fieldName 目标属性
     * @return 目标属性的值
     */
    public static Object getFieldValue(Object object, String fieldName) {
        Object result = null;
        try {
            Method getter = getAccessibleMethodByName(object, "get" + com.kvo.utils.StringUtils.capitalize(fieldName));
            if (getter != null) {
                result = getter.invoke(object);
            } else {
                Field field = getAccessibleField(object, fieldName);
                if (field != null) {
                    result = field.get(object);
                } else {
                    // 无法获取源字符值
                    log.error(String.format("getFieldValue class<%s> field<%s>  is null", object.getClass(), field.getName()));
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(String.format("getFieldValue object<%s> fieldName<%s> error", object.toString(), fieldName), e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 利用反射获取指定对象里面的指定属性
     *
     * @param object    目标对象
     * @param fieldName 目标属性
     * @return 目标字段
     */
    public static Field getField(Object object, String fieldName) {
        Field field = null;
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                log.error(String.format("getField object<%s> fieldName<%s>", object.toString(), fieldName), e);
                throw new RuntimeException(e);
            }
        }
        return field;
    }

    /**
     * 利用反射设置指定对象的指定属性为指定的值
     *
     * @param object     目标对象
     * @param fieldName  目标属性
     * @param fieldValue 目标值
     */
    public static void setFieldValue(Object object, String fieldName, Object fieldValue) {
        Field field = getField(object, fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(object, fieldValue);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error(String.format("setFieldValue object<%s> fieldName<%s> fieldValue<%s>", object.toString(), fieldName, fieldValue.toString()), e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 利用反射设置指定对象的指定属性为指定的值
     *
     * @param field  成员变量
     * @param object 目标对象
     * @param value  目标值
     */
    public static void setFieldValue(@NonNull Field field, @NonNull Object object, @NonNull Object value) {
        String name = field.getName();
        try {
            Method setter = object.getClass().getDeclaredMethod("set" + StringUtils.capitalize(name), value.getClass());
            boolean accessablity = setter.isAccessible();
            setter.setAccessible(true);
            setter.invoke(object, value);
            setter.setAccessible(accessablity);
            return;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("setValue setter class<%s> field<%s> value<%s> error %s", object.getClass(), name, value, e.getMessage()));
            }
            try {
                boolean accessablity = field.isAccessible();
                field.setAccessible(true);
                field.set(object, value);
                field.setAccessible(accessablity);
                return;
            } catch (IllegalAccessException e1) {
                log.error(String.format("setValue field class<%s> field<%s> value<%s>", object.getClass(), name, value), e1);
            }
        }
        log.warn(String.format("setValue not set class<%s> field<%s> value<%s>", object.getClass(), name, value));
    }

    /**
     * 获取利用反射获取类里面的值和名称
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            map.put(fieldName, value);
        }
        return map;
    }

    /**
     * 利用递归调用将Object中的值全部进行获取
     *
     * @param timeFormatStr 格式化时间字符串默认<strong>2017-03-10 10:21</strong>
     * @param obj           对象
     * @param excludeFields 排除的属性
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, String> objectToMapString(String timeFormatStr, Object obj, String... excludeFields) throws IllegalAccessException {
        Map<String, String> map = new HashMap<String, String>();

        if (excludeFields.length != 0) {
            List<String> list = Arrays.asList(excludeFields);
            objectTransfer(timeFormatStr, obj, map, list);
        } else {
            objectTransfer(timeFormatStr, obj, map, null);
        }
        return map;
    }

    /**
     * 递归调用函数
     *
     * @param obj           对象
     * @param map           map
     * @param excludeFields 对应参数
     * @return
     * @throws IllegalAccessException
     */
    private static Map<String, String> objectTransfer(String timeFormatStr, Object obj, Map<String, String> map, List<String> excludeFields) throws IllegalAccessException {
        boolean isExclude = false;
        //默认字符串
        String formatStr = "YYYY-MM-dd HH:mm:ss";
        //设置格式化字符串
        if (timeFormatStr != null && !timeFormatStr.isEmpty()) {
            formatStr = timeFormatStr;
        }
        if (excludeFields != null) {
            isExclude = true;
        }
        Class<?> clazz = obj.getClass();
        //获取值
        for (Field field : clazz.getDeclaredFields()) {
            String fieldName = clazz.getSimpleName() + "." + field.getName();
            //判断是不是需要跳过某个属性
            if (isExclude && excludeFields.contains(fieldName)) {
                continue;
            }
            //设置属性可以被访问
            field.setAccessible(true);
            Object value = field.get(obj);
            Class<?> valueClass = value.getClass();
            if (valueClass.isPrimitive()) {
                map.put(fieldName, value.toString());

            } else if (valueClass.getName().contains(JAVAP)) {//判断是不是基本类型
                if (valueClass.getName().equals(JAVADATESTR)) {
                    //格式化Date类型
                    SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
                    Date date = (Date) value;
                    String dataStr = sdf.format(date);
                    map.put(fieldName, dataStr);
                } else {
                    map.put(fieldName, value.toString());
                }
            } else {
                objectTransfer(timeFormatStr, value, map, excludeFields);
            }
        }
        return map;

    }

    /**
     * 递归遍历对象的所有基础成员变量，并进行操作
     *
     * @param object
     * @param fieldWalkProcessor
     */
    public static Object fieldWalkProcess(@NonNull Object object, @NonNull FieldWalkProcessor fieldWalkProcessor) {
        Class<?> classOfT = object.getClass();
        while (classOfT != Object.class) {
            Field[] fields = classOfT.getDeclaredFields();
            for (Field field : fields) {
                //如果是final或static修饰的字段则不扫描
                if(Modifier.isFinal(field.getModifiers())||Modifier.isStatic(field.getModifiers())){
                    continue;
                }
                Class<?> fieldClass = field.getType();
                if (log.isDebugEnabled()) {
                    log.debug("fieldWalkProcess  class<{}> field<{}>", classOfT.getName(), field.getName());
                }
                boolean accessFlag = field.isAccessible();
                field.setAccessible(true);
                try {
                    Method getMethod = classOfT.getMethod("get" + StringUtils.capitalize(field.getName()));
                    getMethod.setAccessible(true);
                    Object value = getMethod.invoke(object);
                    if (fieldWalkProcessor.isEffected(field, object)) {
                        if ((fieldClass.getName().startsWith("java.lang") || fieldClass.isPrimitive())) {
                            // 基础类型
                            Object result = fieldWalkProcessor.process(field, object, fieldWalkProcessor);
                            setFieldValue(field, object, result);
                        } else if (!fieldClass.getName().startsWith("java.lang")
                                && !Modifier.isStatic(field.getModifiers())) {
                            // 复合类型，获取对象执行对应策略
                            if (!Collection.class.isAssignableFrom(fieldClass)
                                    && !Map.class.isAssignableFrom(fieldClass)
                                    && !fieldClass.isArray()) {
                                // 对象类型
                                if (value != null) {
                                    //值非空，步入递归
                                    fieldWalkProcess(value, fieldWalkProcessor);
                                } else {
                                    //空值，尝试提供处理
                                    Object result = fieldWalkProcessor.process(field, object, fieldWalkProcessor);
                                    if (result != null) {
                                        setFieldValue(field, object, result);
                                    }
                                }
                            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                                // 集合类型
                                if (!CollectionUtils.isEmpty((Collection) value)) {
                                    // 非空集合，步入递归
                                    Class<?> classOfCollection = ObjectUtils.getGenericClass(field.getGenericType());
                                    if (log.isDebugEnabled()) {
                                        log.debug("fieldWalkProcess classOfCollection class<{}> field<{}>", classOfCollection.getName(), field.getName());
                                    }
                                    Collection valueCollection = (Collection) value;
                                    Iterator iterator = valueCollection.iterator();
                                    while (iterator.hasNext()) {
                                        Object obj = iterator.next();
                                        if (log.isDebugEnabled()) {
                                            log.debug("fieldWalkProcess classOfCollection class<{}> field<{}> value<{}>", classOfCollection.getName(), field.getName(), JSON.toJSONString(obj));
                                        }
                                        fieldWalkProcess(classOfCollection.cast(obj), fieldWalkProcessor);
                                    }
                                } else {
                                    //空值，尝试提供处理
                                    Object result = fieldWalkProcessor.process(field, object, fieldWalkProcessor);
                                    if (result != null) {
                                        setFieldValue(field, object, result);
                                    }
                                }
                            } else if (fieldClass.isArray()) {
                                // 数组类型
                                if (value != null) {
                                    // 非空，步入递归
                                    Class<?> classOfArray = field.getType().getComponentType();
                                    int length = Array.getLength(value);
                                    for (int i = 0; i < length; i++) {
                                        Object obj = Array.get(value, i);
                                        if (log.isDebugEnabled()) {
                                            log.debug("DaShuCodeUtils traversedFieldWithAnnotationOperator classOfCollection class<{}> field<{}> value<{}>", classOfArray.getName(), field.getName(), JSON.toJSONString(obj));
                                        }
                                        fieldWalkProcess(classOfArray.cast(obj), fieldWalkProcessor);
                                    }
                                } else {
                                    //空值，尝试提供处理
                                    Object result = fieldWalkProcessor.process(field, object, fieldWalkProcessor);
                                    if (result != null) {
                                        setFieldValue(field, object, result);
                                    }
                                }
                            } else if (Map.class.isAssignableFrom(fieldClass)) {
                                if (!CollectionUtils.isEmpty((Map) value)) {
                                    ((Map) value).forEach((k, v) -> {
                                        Map.Entry entry = new Map.Entry() {

                                            private Object key = k;

                                            private Object value = v;

                                            @Override
                                            public Object getKey() {
                                                return key;
                                            }

                                            @Override
                                            public Object getValue() {
                                                return value;
                                            }

                                            @Override
                                            public Object setValue(Object value) {
                                                return null;
                                            }
                                        };
                                        fieldWalkProcess(entry, fieldWalkProcessor);
                                    });
                                } else {
                                    //空值，尝试提供处理
                                    Object result = fieldWalkProcessor.process(field, object, fieldWalkProcessor);
                                    setFieldValue(field, object, result);
                                }
                            }
                            if (log.isDebugEnabled()) {
                                log.warn("fieldWalkProcess not handle  class<{}> field<{}>", classOfT.getName(), field.getName());
                            }
                        }
                    } else {
                        // 判断不生效
                        if (log.isDebugEnabled()) {
                            log.debug("fieldWalkProcess  class<{}> field<{}> not effected", classOfT.getName(), field.getName());
                        }
                    }
                } catch (NoSuchMethodException e) {
                    // 方法不存在
                    log.error(String.format("traversedFieldWithAnnotationOperator getMethod class<%s> field<%s>", classOfT.getName(), field.getName()), e);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    // 方法调用出错
                    log.error(String.format("traversedFieldWithAnnotationOperator getMethod invoke class<%s> field<%s>", classOfT.getName(), field.getName()), e);
                }
                field.setAccessible(accessFlag);
            }
            // 跳到父类
            classOfT = classOfT.getSuperclass();
        }
        return object;
    }


    /**
     * 成员变量操作接口
     */
    public interface FieldWalkProcessor {

        /**
         * 是否生效操作
         *
         * @param field
         * @param object
         * @return
         */
        boolean isEffected(Field field, Object object);

        /**
         * 成员操作
         *
         * @param field
         * @param object
         */
        Object process(Field field, Object object, FieldWalkProcessor fieldWalkProcessor);
    }


}