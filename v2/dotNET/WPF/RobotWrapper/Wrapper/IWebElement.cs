namespace WebDriver.Wrapper
{
    public interface IWebElement
    {
        void Click();

        string GetAttribute(string attributeName);

        string GetText();

        void SetElementValue(string value);

        bool IsVisible();
    }
}