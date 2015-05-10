package bozic.bajo.weathercast;

import android.test.ProviderTestCase;
import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Created by savo on 8.4.2015.
 */
public class FullTestSuit extends TestSuite {
    public FullTestSuit() {
        super();

    }
    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuit.class).includeAllPackagesUnderHere().build();
    }
}
