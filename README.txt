README - CheepCheep

OAuth Credentials
=================

CheepCheep uses OAuth to authenticate before Twitter.

To prevent abuse, CheepCheep's OAuth credentials are not distributed with the 
source code. Therefore, CheepCheep will fail to compile.

To compile CheepCheep you must first register a new application in Twitter 
and add a new Java class file containing those credentials.

The class should be in the org.tarrio.cheepcheep.http package, and should be 
called OAuthCredentials. It should look like this:

-----
package org.tarrio.cheepcheep.http;

class OAuthCredentials {
  static String CONSUMER_KEY = "contents of consumer key";
  static String CONSUMER_SECRET = "contents of consumer secret";
}
-----
