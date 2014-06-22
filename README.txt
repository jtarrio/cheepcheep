README - CheepCheep

CheepCheep is a lightweight Twitter client for Android.

See CheepCheep's home page at http://jacobo.tarrio.org/cheepcheep


To compile CheepCheep:

1- Import the project into Eclipse.
2- Add new OAuth credentials to the source code.


Importing Into Eclipse
======================

After you have extracted the source code, open Eclipse. Select the "File" menu,
then "Import...", and then "Existing Projects into Workspace", and then select
the location where the source code resides.


OAuth Credentials
=================

CheepCheep uses OAuth to authenticate before Twitter.

To prevent abuse, CheepCheep's OAuth credentials are not distributed with the 
source code. Therefore, CheepCheep will fail to compile unless you install a
new set of credentials.

You must first register a new application in Twitter, and then add a new Java
class file containing the OAuth credentials Twitter will give you.

The class should be in the org.tarrio.cheepcheep.http package, and should be 
called OAuthCredentials. It should look like this:

-----
package org.tarrio.cheepcheep.http;

class OAuthCredentials {
  static String CONSUMER_KEY = "contents of consumer key";
  static String CONSUMER_SECRET = "contents of consumer secret";
}
-----
