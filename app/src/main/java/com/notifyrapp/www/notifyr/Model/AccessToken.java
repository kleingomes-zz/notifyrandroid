package com.notifyrapp.www.notifyr.Model;

import android.media.session.MediaSession;

import java.util.Date;

/**
 * Created by K on 11/7/2016.
 */

public class AccessToken {

    private String Value;
    private Date Issued;
    private Date Expired;
    private String TokenType;
    private long ExpiresIn;

    /* Getters and Setters */
    public String getTokenValue() { return Value; }
    public void setTokenValue(String Value) { this.Value = Value; }

    public Date getIssuedDate() { return Issued; }
    public void setIssuedDate(Date Issued) { this.Issued = Issued; }

    public Date getExpiryDate() { return Expired; }
    public void setExpiryDate(Date expiryDate) { this.Expired = expiryDate; }

    public String getTokenType() { return TokenType; }
    public void setTokenType(String tokenType) { this.TokenType = TokenType; }

    public long getExpiresInDate() { return ExpiresIn; }
    public void setExpiresInDate(long ExpiresInDate) { this.ExpiresIn = ExpiresInDate; }


}
//[0]	(null)	@"userName" : @"7a83c8cc-d3b9-48d0-bc6e-827c94266ea9@notifyr.ca"
 //       [1]	(null)	@".issued" : @"Tue, 08 Nov 2016 02:00:15 GMT"
  //      [2]	(null)	@".expires" : @"Tue, 22 Nov 2016 02:00:15 GMT"
    //    [3]	(null)	@"token_type" : @"bearer"
      //  [4]	(null)	@"access_token" : @"9XSte5EGrU-5Fv_bEItYswfz8sMuGjTK2YBLQgV5mNJte7-nLgoLrIaPNquj_dMLQkoRFdyTQq0bLTPNYgqEgwGtEI3EzaBnJ4nc7c6FcnfbzCH-7OA3nQhG0uy3bMJ-_Ov2P94UWn_SIBipV-MGQjr2eHCL8B5x0N26VrRlvIk_a-PkBvyHKNw2N0I-LHoHziBtOCGmzjXZK8vndecmtzDg-CjrHgugSaP1tU4cwXmGZEG-be7TFeWYbd5XnO861F71GirjXIsyXvVkzZWuVlDSsygCPITPN3nOeUAH_wYkdXc-j9pDGK6pqj6C2LT6k9oFKqN6R53sDGnp-EHPR3DrKqTJFo7QMTOtJaPY9Kg2Hinmn6W7JnN7TD0NgA_KXdwDMw1Jmk8EmstcdkKhn-dYvtl07GwN0WshLcs-iq0zW-XDmqPD9D7S6_rxZJ2MPE3Ri7s2VB5RH7lQgD24cuYOGzzPYMRtpjvxbqqQ_QY"
       // [5]	(null)	@"expires_in" : (long)1209599