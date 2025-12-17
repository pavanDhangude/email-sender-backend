package com.emailsenser.EmailSender;

import com.emailsenser.EmailSender.helper.Message;
import com.emailsenser.EmailSender.services.EmailService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
public class EmailSenderTest {
    @Autowired
    private EmailService emailService;
    @Test
    void emailSendTest(){
       System.out.println("Sending Email ");
       emailService.sendEmail(" pavandhangude28@gmail.com","Email from SpringBoot", "This email is send using springboot while create email service.");
}
@Test
void sendHtmlInEmail(){
        String html = "" +
                "<h1 style='color:red;border:1px solid red;'>Welcome to learn code with Pavan</h1>" +
                "";
        emailService.sendEmailWithHtml("pavandhangude28@gmail.com","Email From SpringBoot",html);
}
@Test
void sendEmailWithFile(){
        emailService.sendEmailWithFile("pavandhangude28@gmail.com",
                "Email With File",
                "This Email contains file",
                new File("C:\\Users\\Shree\\OneDrive\\Desktop\\intelijproject\\EmailSender\\src\\main\\resources\\static\\profile.png")
        );
}

    @Test
    void sendEmailWithFileWithStream() {

        File file = new File("C:\\Users\\Shree\\OneDrive\\Desktop\\intelijproject\\EmailSender\\src\\main\\resources\\static\\profile.png");
        try {
            InputStream is = new FileInputStream(file);
            emailService.sendEmailWithFile("pavandhangude28@gmail.com",
                    "Email With File",
                    "This Email contains file",is

                    );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }


    //receiving email test
    @Test
    void getInbox(){

       List<Message> inboxMessages = emailService.getInboxMessages();
       inboxMessages.forEach(item->{
           System.out.println(item.getSubjects());
           System.out.println(item.getContent());
           System.out.println(item.getFiles());
           System.out.println("------------");
       });
    }




}
