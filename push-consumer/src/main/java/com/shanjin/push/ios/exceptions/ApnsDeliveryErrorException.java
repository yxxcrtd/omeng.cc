package com.shanjin.push.ios.exceptions;

import com.shanjin.push.ios.apns.DeliveryError;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author kkirch
 */
public class ApnsDeliveryErrorException extends ApnsException {

    private final DeliveryError deliveryError;

    public ApnsDeliveryErrorException(DeliveryError error) {
        this.deliveryError = error;
    }

    @Override
    public String getMessage() {
        return "Failed to deliver notification with error code " + deliveryError.code();
    }

    public DeliveryError getDeliveryError() {
        return deliveryError;
    }
    
    
}
