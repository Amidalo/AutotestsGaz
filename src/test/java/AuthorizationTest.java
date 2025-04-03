import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;

public class AuthorizationTest {
    private WebDriver webDriver;
    private WebDriverWait wait;
    private static final int DELAY_MS = 1000;

    @BeforeTest
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        webDriver = new ChromeDriver(options);
        wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));
    }

    @DataProvider(name = "Недействительные email")
    public Object[][] provideInvalidEmails() {
        return new Object[][]{
                {"user@.com", "123456"},
                {"usergmail.com", "123456"},
                {"user@gmail..com", "123456"},
                {"user@yandex@ru", "123456"},
                {"user name@gmail.com", "123456"},
                {"", "123456"},
                {"@gmail.com", "123456"},
                {"user!@mail.ru", "123456"},
                {"user@gmail.c", "123456"},
                {"user@gmail.123", "123456"},
                {"пользователь@яндекс.рф", "123456"},
                {"aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeeffffffffffggggg@gmail.com", "123456"}
        };
    }

    @Test(dataProvider = "Недействительные email", enabled = true, priority = 1,
            description = "Проверка сообщений валидации, о неверных данных или о пустых полях email")
    public void testInvalidEmailValidation(String email, String password) {
        webDriver.get("https://same-67fegpdq2ll-latest.netlify.app/");
        webDriver.manage().window().maximize();

        try {
            WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input[type='email']")));

            checkValidationState(emailField, false, "Поле должно быть валидным до ввода");

            emailField.clear();
            emailField.sendKeys(email);
            sleep(DELAY_MS);

            WebElement passwordField = webDriver.findElement(By.cssSelector("input[type='password']"));
            passwordField.clear();
            passwordField.sendKeys(password);
            sleep(DELAY_MS);

            WebElement submitButton = webDriver.findElement(By.cssSelector("button[type='submit']"));
            submitButton.click();
            sleep(DELAY_MS * 2);

            boolean isHtml5Invalid = !(boolean) ((JavascriptExecutor) webDriver)
                    .executeScript("return arguments[0].checkValidity();", emailField);

            boolean isAuthErrorShown = checkForAuthError();
            boolean isEmptyFieldErrorShown = checkForEmptyFieldError();

            Assert.assertTrue(isHtml5Invalid || isAuthErrorShown || isEmptyFieldErrorShown,
                    "Ожидалось одно из сообщений: валидации, о неверных данных или о пустых полях");

        } catch (Exception e) {
            Assert.fail("Тест завершился ошибкой: " + e.getMessage());
        }
    }

    private boolean checkForAuthError() {
        try {
            By errorLocator = By.xpath("//li//div[contains(text(), \"Неверный email или пароль\")]");
            WebElement errorMessage = webDriver.findElement(errorLocator);
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkForEmptyFieldError() {
        try {
            By errorLocator = By.xpath("//li//div[contains(text(), \"Пожалуйста, заполните все поля\")]");
            WebElement errorMessage = webDriver.findElement(errorLocator);
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void checkValidationState(WebElement field, boolean shouldBeInvalid, String message) {
        boolean isValid = (boolean) ((JavascriptExecutor) webDriver)
                .executeScript("return arguments[0].checkValidity();", field);

        if (shouldBeInvalid) {
            Assert.assertFalse(isValid, message);
            String validationMessage = getValidationMessage(field);
            Assert.assertNotNull(validationMessage, "Сообщение валидации не должно быть null");
        } else {
            Assert.assertTrue(isValid, message);
        }
    }

    private String getValidationMessage(WebElement field) {
        return (String) ((JavascriptExecutor) webDriver)
                .executeScript("return arguments[0].validationMessage;", field);
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

//    @DataProvider(name = "Недействительные пароли")
//    public Object[][] providePasswordData() {
//        return new Object[][] {
//                // email,                          password,            expectedMessage
//                {"user@gmail.com",                 "12345",         "Пароль должен содержать минимум 6 символов"},
//                {"test@mail.ru",                   "qwert",         "Пароль должен содержать минимум 6 символов"},
//                {"example@yandex.ru",              "        ",      "Некорректный символ в пароле: пробел, кавычки, двоеточие"},
//                {"admin@domain.com",               "1",             "Пароль должен содержать минимум 6 символов"},
//                {"support@company.net",            "",              "Пароль должен содержать минимум 6 символов"},
//                {"support@company.net",            "sdferr:",       "Некорректный символ в пароле: пробел, кавычки, двоеточие"},
//                {"support@company.net",            "'sdferr'",      "Некорректный символ в пароле: пробел, кавычки, двоеточие"},
//        };
//    }

    // ТК-10
//    @Test(dataProvider = "Недействительные пароли", enabled = false, priority = 1,
//            description = "Проверка сообщений об ошибках авторизации при невалидном пароле")
//    public void testPasswordErrors(String email, String password, String expectedMessage) {
//        webDriver.get("https://same-v739m8ov3qm-latest.netlify.app/login");
//        webDriver.manage().window().maximize();
//
//        try {
//            TimeUnit.SECONDS.sleep(1);
//
//            // 1 действие - вводим нашу почту
//            WebElement emailField = webDriver.findElement(By.xpath("//div//input[@name=\"email\"]"));
//            emailField.sendKeys(email);
//            TimeUnit.SECONDS.sleep(1);
//
//            // 2 действие - вводим наш пароль
//            WebElement passwordField = webDriver.findElement(By.xpath("//div//input[@name=\"password\"]"));
//            passwordField.sendKeys(password);
//            TimeUnit.SECONDS.sleep(1);
//
//            // 3 действие - нажимаем по кнопке 'Войти' на странице входа
//            WebElement submitButton = webDriver.findElement(
//                    By.xpath("//div//button[@type=\"submit\"]")
//            );
//            submitButton.click();
//            TimeUnit.SECONDS.sleep(3);
//
//            By errorLocator = By.xpath("//p[contains(text(), 'Пароль должен содержать минимум 6 символов')]");
//            // Проверка сообщения
//            WebElement errorMessage = webDriver.findElement(errorLocator);
//            Assert.assertEquals(errorMessage.getText(), expectedMessage);
//
//        } catch (InterruptedException e) {
//            Assert.fail("Тест был прерван: " + e.getMessage());
//        } catch (NoSuchElementException e) {
//            Assert.fail("Возможно сообщение c предупреждением еще не успело появиться: " + e.getMessage() +
//                    "\n\nПопробуйте увеличить время паузы.");
//        }
//    }

    @AfterTest
    public void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
}
