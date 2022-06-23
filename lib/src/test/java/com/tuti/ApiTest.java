/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.tuti;

import com.tuti.api.TutiApiClient;
import com.tuti.api.authentication.*;
import com.tuti.api.data.Card;
import com.tuti.api.data.Cards;
import com.tuti.util.Utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import static org.junit.Assert.*;


public class ApiTest {
     static TutiApiClient client;

    @BeforeAll
    public static void setup(){
        client = new TutiApiClient();
        client.setSingleThreaded(true);
    }

    @BeforeEach
    public void  signIn(){
        SignInRequest credentials = new SignInRequest("0999999999","Testtest1234.");

        client.SignIn(credentials,( signInResponse , rawResponse) -> {
            client.setAuthToken(signInResponse.getAuthorizationJWT());
        },(objectReceived, exception, rawResponse) -> {fail("sign in failed for @BeforeClass");});
    }
    @Test
    public void testSignInApi(){
        SignInRequest credentials = new SignInRequest("adonese","12345678");
        client.setAuthToken(null);

        client.SignIn(credentials,( signInResponse , rawResponse) -> {
            User user = signInResponse.getUser();

            System.out.println("User information from sign in endpoint (adonese case):\n"+user);
            assertEquals("adonese",user.getUsername());
            assertEquals("Mohamed Yousif",user.getFullname());
            assertEquals("mmbusif@gmail.com",user.getEmail());
            assertEquals("0925343834",user.getMobileNumber());
            assertTrue(user.getIsMerchant());
            assertEquals(0,user.getId());
        },( error, exception, rawResponse) -> {fail("sign in failed");});

        credentials = new SignInRequest("non_existent_user" + Utils.generateRandomAlphanumericString(8),"asjfkdlj");
        client.SignIn(credentials,( signInResponse , rawResponse) -> {
            fail("sign in failed because the user is non existent");
        },( error, exception, rawResponse) -> {
            if (error != null) System.out.println("\nNon existent user case:\n"+error);
            if (exception != null ) fail("exception in sign in");
        });
    }

    @Test public void testSignUpApi(){
        SignUpRequest info = new SignUpRequest();

        String tag = Utils.generateRandomNumericString(8);

        String mobileNumber = "02" + Utils.generateRandomNumericString(8);
        String username = "test_" + tag;
        String email = "test_" + tag +"@test.com";
        String fullname = Utils.generateARandomName() + " " + Utils.generateARandomName();
        String password = Utils.generateRandomAlphanumericString(12)+"A1.";
        boolean isMerchant = new Random().nextBoolean();
        info.setMobileNumber(mobileNumber);
        info.setPassword(password);
        info.setUsername(username);
        info.setFullname(fullname);
        info.setEmail(email);
        info.setMerchant(isMerchant);
        client.Signup(info,( signUpResponse, rawResponse) -> {

            User user = signUpResponse.getUser();
            System.out.println("User information from signup endpoint:\n"+user);

            assertEquals(username,user.getUsername());
            assertEquals(fullname,user.getFullname());
            assertEquals(email,user.getEmail());
            assertEquals(mobileNumber,user.getMobileNumber());
            assertEquals(isMerchant,user.getIsMerchant());

        },( error, exception, rawResponse ) -> {
            fail("sign up failed");} );

        SignInRequest creds = new SignInRequest(username,password);
        client.SignIn(creds , (objectReceived, rawResponse) -> {
                    User user = objectReceived.getUser();
                    System.out.println("User information from sign in endpoint:\n"+user);

                    assertEquals(username,user.getUsername());
                    assertEquals(fullname,user.getFullname());
                    assertEquals(email,user.getEmail());
                    assertEquals(mobileNumber,user.getMobileNumber());
                    assertEquals(isMerchant,user.getIsMerchant());

                } ,
                (errorReceived, exception, rawResponse) -> {fail("sign in failed!");} );

    }

    @Test public void testAddCardAndGetCard(){
        //generate random values for the tets
        String name = Utils.generateRandomAlphanumericString(16);
        String PAN = Utils.generateRandomNumericString(16);
        String expiryDate = Utils.generateRandomNumericString(4);

        //create a card object to hold the cards data
        Card cardToAdd = new Card();
        cardToAdd.setName(name);
        cardToAdd.setExpiryDate(expiryDate);
        cardToAdd.setPAN(PAN);

        System.out.println("Card to add" + cardToAdd);

        client.addCard(cardToAdd,(objectReceived, rawResponse) -> {},
                (errorReceived, exception, rawResponse) -> {fail("adding a card failed");});

        //get the cards from the api to assert that it was added successfully
        client.getCards((cards,response) -> {
            outputCardsInfo(cards);

            boolean fail = true;
            for (Card card : cards.getCards()){
                if (card.equals(cardToAdd)){
                    fail = false;
                }
            }
            if (fail) fail("card was not found!");

        } , (objectReceived, exception, rawResponse) -> {fail();});
    }

    public void outputCardsInfo(Cards cards){
        System.out.println("Cards associated with the account");
        for (Card card : cards.getCards()){
            System.out.println(card);
        }
    }
    @Test public void testIPINBlockGenerator(){
        //System.out.println(new IPIN().getIPINBlock("0000","MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==", UUID.randomUUID().toString()));
    }


}
