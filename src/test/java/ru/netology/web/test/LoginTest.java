package ru.netology.web.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.data.SQLHelper;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest {
    private static final String URL = "http://localhost:9999";

    @AfterEach
    void tearDown() {
        SQLHelper.cleanAuthCodes();
    }

    @AfterAll
    static void tearDownAll() {
        SQLHelper.cleanDatabase();
    }

    @Test
    void shouldLoginWithCodeFromDatabase() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = open(URL, LoginPage.class).validLogin(authInfo);
        var code = SQLHelper.getVerificationCode(authInfo);
        verificationPage.validVerify(code);
    }

    @Test
    void shouldGetErrorForRandomUser() {
        var randomUser = DataHelper.generateRandomUser();
        var loginPage = open(URL, LoginPage.class);
        loginPage.login(randomUser);
        loginPage.verifyErrorNotification("Ошибка! Неверно указан логин или пароль");
    }

    @Test
    void shouldGetErrorForWrongVerificationCode() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = open(URL, LoginPage.class).validLogin(authInfo);
        verificationPage.verify(DataHelper.generateRandomVerificationCode());
        verificationPage.verifyErrorNotification("Ошибка! Неверно указан код! Попробуйте ещё раз.");
    }

    @Test
    void shouldBlockUserAfterThreeWrongPasswords() {
        var user = DataHelper.getSecondAuthInfoWithTestData();
        var wrongUser = new DataHelper.AuthInfo(user.getLogin(), DataHelper.generateRandomUser().getPassword());
        for (int i = 0; i < 3; i++) {
            open(URL, LoginPage.class).login(wrongUser);
        }
        assertEquals("blocked", SQLHelper.getUserStatus(user.getLogin()));
    }
}
