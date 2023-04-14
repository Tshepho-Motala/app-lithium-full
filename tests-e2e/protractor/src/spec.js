// spec.js content e.g.
describe('ui-network-admin', function () {
	it('navigates to the site', function () {
		browser.get(browser.params.url);
		element(by.model('controller.credentials.username')).sendKeys("admin");
		element(by.model('controller.credentials.password')).sendKeys("Gauteng");
		element(by.model('controller.credentials.password')).submit();
		browser.sleep(5000);
		var pageTitle = element(by.className('page-header'));
		expect(pageTitle.getText()).toBe('Domain Dashboard');
	});
});
