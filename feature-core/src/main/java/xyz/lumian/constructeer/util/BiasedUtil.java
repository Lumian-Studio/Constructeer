package xyz.lumian.constructeer.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.NullUnmarked;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;



//**********************************************************************************************************************
@NullUnmarked
public class BiasedUtil
{
    //******************************************************************************************************************
    public static boolean isClient() { return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT; }
    public static boolean isServer() { return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER; }
    
    //==================================================================================================================
    public static <T1, T2> T2 map(final T1 obj, final Function<T1, T2> ifClient, final Function<T1, T2> ifServer)
    {
        return (BiasedUtil.isClient() ? ifClient : ifServer).apply(obj);
    }
    
    public static void run(final Runnable ifClient, final Runnable ifServer)
    {
        (BiasedUtil.isClient() ? ifClient : ifServer).run();
    }
    
    public static <T> void accept(final T obj, final Consumer<T> ifClient, final Consumer<T> ifServer)
    {
        (BiasedUtil.isClient() ? ifClient : ifServer).accept(obj);
    }
    
    public static <T> T get(final T ifClient, T ifServer)
    {
        return (BiasedUtil.isClient() ? ifClient : ifServer);
    }
    
    public static <T> T getLazy(final Supplier<T> ifClient, final Supplier<T> ifServer)
    {
        return (BiasedUtil.isClient() ? ifClient : ifServer).get();
    }
    
    public static <T> T load(
        @Language("jvm-class-name")
        final String   clientClass,
        final Class<T> mutualClass,
        final Object   ...args
    )
    {
        final Class<?>[] arg_types = Arrays.stream(args)
            .map(Object::getClass)
            .toArray(Class<?>[]::new);
        
        final Constructor<?> ctor = BiasedUtil.getLazy(
            (() ->
            {
                try
                {
                    final Class<?> manager_class = Class.forName(clientClass, true, mutualClass.getClassLoader());
                    return manager_class.getConstructor(arg_types);
                }
                catch (final Exception ex) { throw new RuntimeException(ex); }
            }),
            (() ->
            {
                try                        { return mutualClass.getConstructor(arg_types); }
                catch (final Exception ex) { throw new RuntimeException(ex); }
            }));
        
        try
        {
            return mutualClass.cast(ctor.newInstance(args));
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
