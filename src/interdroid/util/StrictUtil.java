package interdroid.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper class to allow us to turn on StrictMode on devices which support
 * it but leave the calls in for devices which do not.
 *
 * This class uses reflection to setup StrictMode on devices which support it.
 *
 * @author nick &gt;palmer@cs.vu.nl&lt;
 *
 */
public final class StrictUtil {
    /**
     * Access to logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(StrictUtil.class);

    /**
     * No construction.
     */
    private StrictUtil() {
        // No construction
    }

    /**
     * Is developer mode on.
     */
    private static boolean sDeveloperMode = false;

    /**
     * Turns on developer mode system wide.
     */
    public static void setDeveloperMode() {
        sDeveloperMode = true;
    }

    /**
     * Sets the thread policy for strict mode to all with
     * log and death penalties.
     */
    public static void setThreadPolicies() {
        if (sDeveloperMode) {
            Class<?> strictMode;
            try {
                strictMode =
                        Class.forName(
                                "android.os.StrictMode");

                setThreadPolicy(strictMode);
            } catch (Exception e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Exception setting Strict ThreadPolicy", e);
                }
            }
        }
    }

    /**
     * Sets the thread and vm policy for strict mode to all with log
     * and death penalties.
     */
    public static void setDeveloperPolicies() {
        if (sDeveloperMode) {
            Class<?> strictMode;
            try {
                strictMode =
                        Class.forName(
                                "android.os.StrictMode");

                setThreadPolicy(strictMode);
                setVmPolicy(strictMode);
            } catch (Exception e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Exception setting thread and vm policy", e);
                }
            }
        }
    }

    /**
     * Sets the vm policy.
     *
     * @param strictMode
     *            the strict mode to work with
     * @throws ClassNotFoundException
     *             on problem
     * @throws NoSuchMethodException
     *             on problem
     * @throws InstantiationException
     *             on problem
     * @throws IllegalAccessException
     *             on problem
     * @throws InvocationTargetException
     *             on problem
     */
    private static void setVmPolicy(final Class<?> strictMode)
            throws ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Class<?> vmPolicyType =
                Class.forName("android.os.StrictMode$VmPolicy");
        Method setVmPolicy =
                strictMode.getMethod("setVmPolicy",
                        vmPolicyType);
        Class<?> vmPolicyBuilder = Class
                .forName(
                    "android.os.StrictMode$VmPolicy$Builder");

        Object policy = buildPolicy(vmPolicyBuilder);
        setVmPolicy.invoke(strictMode, policy);
    }

    /**
     * Sets the thread policy.
     *
     * @param strictMode
     *            the strict mode to work with
     * @throws ClassNotFoundException
     *             on problem
     * @throws NoSuchMethodException
     *             on problem
     * @throws InstantiationException
     *             on problem
     * @throws IllegalAccessException
     *             on problem
     * @throws InvocationTargetException
     *             on problem
     */
    private static void setThreadPolicy(final Class<?> strictMode)
            throws ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {

        Class<?> threadPolicyType = Class
                .forName("android.os.StrictMode$ThreadPolicy");
        Method setThreadPolicy = strictMode.getMethod("setThreadPolicy",
                threadPolicyType);
        Class<?> threadPolicyBuilder = Class
                .forName(
                    "android.os.StrictMode$ThreadPolicy$Builder");

        Object policy = buildPolicy(threadPolicyBuilder);
        setThreadPolicy.invoke(strictMode, policy);
    }

    /**
     * Builds a policy with detectAll, penaltyLog and penaltyDeath.
     *
     * @param policyBuilder
     *            the builder to use
     * @return the policy object
     * @throws NoSuchMethodException
     *             on problem
     * @throws InstantiationException
     *             on problem
     * @throws IllegalAccessException
     *             on problem
     * @throws InvocationTargetException
     *             on problem
     */
    private static Object buildPolicy(final Class<?> policyBuilder)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        Method detectAll = policyBuilder.getMethod("detectAll");
        Method penaltyLog = policyBuilder.getMethod("penaltyLog");
        Method penaltyDeath = policyBuilder.getMethod("penaltyDeath");
        Method build = policyBuilder.getMethod("build");
        Constructor<?> constructor = policyBuilder.getConstructor();

        Object builder = constructor.newInstance();
        builder = detectAll.invoke(builder);
        builder = penaltyLog.invoke(builder);
        builder = penaltyDeath.invoke(builder);
        return build.invoke(builder);
    }
}
