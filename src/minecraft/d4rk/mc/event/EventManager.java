package d4rk.mc.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import d4rk.mc.Hack;
import d4rk.mc.event.listener.*;
import d4rk.mc.event.listener.secretcraft.GMParser;
import d4rk.mc.event.listener.secretcraft.PMParser;
import d4rk.mc.util.Pair;

/**
 * This class is a singleton, so all of it functions are static. Furthermore its
 * constructor is private.
 */
public class EventManager
{
    private EventManager()
    {
    }

    /**
     * Call this method to fire an event. All of its listeners are called.
     *
     * @param event
     *            The event to be fired.
     * @return {@link DisableEvent#isDisabled() event.isDisabeled()}
     */
    public static boolean fireEvent(BaseEvent event)
    {
        List<Pair<EventListener, Method>> l = map.get(event.getClass());

        if (l == null && event instanceof DisableEvent)
        {
            return ((DisableEvent) event).isDisabled();
        }

        if (l == null)
        {
            return false;
        }

        for (Pair<EventListener, Method> p : l)
        {
            try
            {
                p.getSecond().invoke(p.getFirst(), event);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }

        if (event instanceof DisableEvent)
        {
            return ((DisableEvent) event).isDisabled();
        }

        return false;
    }

    /**
     * Registers all Methods of the listener with one parameter as an event
     * listener method. The parameter's class must be a subclass of
     * {@link BaseEvent}.
     *
     * @param listener
     */
    public static void registerEvents(EventListener listener)
    {
        Method[] methods = listener.getClass().getMethods();

        for (Method m : methods)
        {
            Class<?>[] params = m.getParameterTypes();

            if (params.length != 1)
            {
                continue;
            }

            if (params[0].getName().startsWith("["))
            {
                continue;
            }

            if (!isSubClassOf(params[0], BaseEvent.class))
            {
                continue;
            }

            List l = map.get(params[0]);

            if (l == null)
            {
                l = new ArrayList<Pair<EventListener, Method>>();
                map.put(params[0], l);
            }

            Hack.logToFile("[EventManager] Successfully registered '"
                    + afterLastDot(listener.getClass().getName()) + "."
                    + m.getName() + "(" + afterLastDot(params[0].getName())
                    + ")'");
            l.add(new Pair(listener, m));
        }
    }

    public static void reloadListeners()
    {
        map.clear();
        // TODO: dynamically loading listeners from a file containing classpaths
        registerEvents(new InventoryHelper());
        registerEvents(new CommandParser());
        registerEvents(new PMParser());
        registerEvents(new PMIgnore());
        registerEvents(new PMLogger());
        registerEvents(new GMParser());
        registerEvents(new GMLogger());
        registerEvents(new AutoHierWohntSign());
        registerEvents(new ChestShopAnalyzer());
        registerEvents(new ParticelMarker());
    }

    private static boolean isSubClassOf(Class check, Class sup)
    {
        Class sup2 = check;

        while (!sup.equals(sup2))
        {
            sup2 = sup2.getSuperclass();

            if (sup2 == null)
            {
                return false;
            }
        }

        return true;
    }

    private static String afterLastDot(String str)
    {
        return str.substring(str.lastIndexOf('.') + 1);
    }

    private static HashMap<Class, List<Pair<EventListener, Method>>> map = new HashMap();
}
