package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {
    private final SelenideElement codeField = $("[data-test-id=code] input");
    private final SelenideElement verifyButton = $("[data-test-id=action-verify]");
    private final SelenideElement errorNotification = $("[data-test-id=error-notification] .notification__content");

    public VerificationPage() {
        codeField.shouldBe(visible);
    }

    public DashboardPage validVerify(DataHelper.VerificationCode code) {
        verify(code);
        return new DashboardPage();
    }

    public void verify(DataHelper.VerificationCode code) {
        codeField.setValue(code.getCode());
        verifyButton.click();
    }

    public void verifyErrorNotification(String expectedText) {
        errorNotification.shouldHave(text(expectedText)).shouldBe(visible);
    }
}
