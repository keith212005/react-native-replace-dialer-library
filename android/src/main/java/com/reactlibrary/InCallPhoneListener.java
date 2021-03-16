package com.reactlibrary;

import android.provider.ContactsContract.CommonDataKinds.Phone;

/**
 * Interface implemented by In-Call components that maintain a reference to the Telecomm API
 * {@code Phone} object. Clarifies the expectations associated with the relevant method calls.
 */
public interface InCallPhoneListener {
    /**
     * Called once at {@code InCallService} startup time with a valid {@code Phone}. At
     * that time, there will be no existing {@code Call}s.
     *
     * @param phone The {@code Phone} object.
     */
    void setPhone(Phone phone);
    /**
     * Called once at {@code InCallService} shutdown time. At that time, any {@code Call}s
     * will have transitioned through the disconnected state and will no longer exist.
     */
    void clearPhone();
}