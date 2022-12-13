package com.future.reggie.common;

/**
 * @author guorui
 * @create 2022-12-06-9:09
 */

// 本工具类用于在多个共享一个线程的方法中间传递数据，threadLocal提供了一个存储空间，只要多个方法共享一个线程，就都可以读取这个空间中存储的内容
// LoginCheckFilter 和 controller的保存和更新方法，以及MyMetaObjectHandler自动填充方法都是共享一个线程的方法
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
