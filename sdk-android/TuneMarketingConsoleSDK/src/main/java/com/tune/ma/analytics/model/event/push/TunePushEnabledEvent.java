package com.tune.ma.analytics.model.event.push;

import com.tune.ma.analytics.model.constants.TuneEventType;
import com.tune.ma.analytics.model.event.TuneAnalyticsEventBase;

/**
 * Created by charlesgilliam on 2/10/16.
 * @deprecated IAM functionality. This method will be removed in Tune Android SDK v6.0.0
 */
@Deprecated
public class TunePushEnabledEvent extends TuneAnalyticsEventBase {
    public TunePushEnabledEvent(boolean status) {
        super();

        setCategory(APPLICATION_CATEGORY);
        setAction(status ? "Push Enabled" : "Push Disabled");
        setEventType(TuneEventType.EVENT);
    }
}
