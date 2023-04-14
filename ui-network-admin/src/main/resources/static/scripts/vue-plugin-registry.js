/**
 * Main registry for all Vue Plugins
 */
class VuePluginRegistry {
    // Add all Vue Plugins into this list
    // DEPRECATED - Use Routes instead
    vuePlugins = [
        new VuePlugin("plugin-ManageDocumentTypesTab", "#vueMountDocTypes", "dashboard/brand/config/doc-types"),
        new VuePlugin("plugin-LastXCashierTransactions", "#vueMountLastXCashierTransactions", "dashboard/cashier/transactions/view"),
        new VuePlugin("plugin-DocumentsTab", "#vueMountDocsList", "dashboard/players/documents-list"),
        new VuePlugin("plugin-DocumentUploadQuickAction", "#vueMountDocUploadQuickAction", "dashboard/players/document-upload-quick-action"),
        new VuePlugin("plugin-BankAccountLookupTable", "#vueMountBankAccountLookupTable", "dashboard/cashier/bank-account-lookup/table"),
        new VuePlugin("plugin-BankAccountLookup", "#vueMountBankAccountLookup", "dashboard/cashier/bank-account-lookup/page"),
        new VuePlugin("plugin-CashierConfig", "#vueMountCashierConfig", "dashboard/cashier/config"),

        // Adding custom elements
        new VuePlugin("plugin-PageHeader", "#vueMountPageHeader", "page-header"),
        new VuePlugin("plugin-QuickActions", "#vueMountQuickActions", "QuickActions"),

        // Generic dialogs - never delete this!
        new VuePlugin('util-dialogGeneric', '#vueUtilDialogGeneric', 'main'),
        new VuePlugin('util-dialogConfirm', '#vueUtilDialogConfirm', 'main'),

        //CMS
        new VuePlugin('plugin-CasinoCmsImages', '#vueMountCasinoCmsImage', 'dashboard/casino/cms-images'),
        new VuePlugin('plugin-UploadCmsAssets', '#vueMountUploadCmsAssets', 'dashboard/cmsassets'),

        //CSV
        new VuePlugin('plugin-CsvExport', '#vueMountCsvExport', 'dashboard/csv-export'),

        //CSV
        new VuePlugin('plugin-PlayerThresholdExport', '#vueMountPlayerThresholdExport', 'PlayerThresholdExport'),
        
        // Cashier
        new VuePlugin("plugin-AutoWithdrawalDetailPage", "#vueMountAutoWithdrawalDetailPage", "AutoWithdrawalDetailPage"),
        new VuePlugin("plugin-AutoWithdrawalPage", "#vueMountAutoWithdrawal", "AutoWithdrawalPage"),

        // CASHIER Transaction
        new VuePlugin("plugin-TransactionsDetailPage", "#vueMountTransactionsDetailPage", "TransactionsDetailPage"),
        new VuePlugin("plugin-TransactionsListPage", "#vueMountTransactionsListPage", "TransactionsListPage"),
        new VuePlugin("plugin-WithdrawalBulk", "#vueMountWithdrawalBulk", "WithdrawalBulk"),
        //  Adjustment transaction
        new VuePlugin("plugin-BalanceAdjustmentsTransaction", "#vueBalanceAdjustmentsTransaction", "BalanceAdjustmentsTransaction"),

        //Player
        new VuePlugin("plugin-PlayerSearchTopBar", "#vueMountPlayerSearchTopBar", "PlayerSearchTopBar"),
        new VuePlugin("plugin-PlayerLinksSearchTopBar", "#vueMountPlayerLinksSearchTopBar", "PlayerLinksSearchTopBar"),
        new VuePlugin("plugin-DomainSelect", "#vueMountDomainSelect", "DomainSelect"),
        new VuePlugin("plugin-PlayerKYCVendorModal", "#vueMountPlayerKYCVendorModal", "PlayerKYCVendorModal"),

        new VuePlugin("plugin-PlayerKYCVendorModal", "#vueMountPlayerKYCVendorModal", "PlayerKYCVendorModal"),

        //Bonuses
        new VuePlugin("plugin-BonusSearchTopBar", "#vueMountBonusSearchTopBar", "BonusSearchTopBar"),

        // Email
        new VuePlugin("plugin-MailSendDialog", "#vueMountMailSendDialog", "MailSendDialog"),

        //Promotions
        new VuePlugin("plugin-Promotions", "#vueMountPromotions", "Promotions-beta"),

        //Promotions
        new VuePlugin("plugin-RewardPlayerHistory", "#vueMountRewardPlayerHistory", "Promotions-Reward-Player-History"),

        //Games
        new VuePlugin("plugin-GameSearchTopBar", "#vueMountGameSearchTopBar", "GameSearchTopBar"),
        new VuePlugin("plugin-Rewards", "#vueMountRewards", "Rewards"),

        // BalanceMovement
        new VuePlugin("plugin-BalanceMovement", "#vueMountBalanceMovement", "BalanceMovement"),

        // PlayerProtection
        new VuePlugin("plugin-PlayerProtection", "#vueMountPlayerProtection", "PlayerProtection"),

        // Banners
        new VuePlugin("plugin-BannerManagement", "#vueMountBannerManagement", "BannerManagement"),

        // Threshold
        new VuePlugin("plugin-PlayerThresholdHistory", "#vueMountPlayerThresholdHistory", "PlayerThresholdHistory")

    ]

    vueCssRef = []

    loadByChunk(chunk) {
        const plugin = this.vuePlugins.find((p) => p.chunk === chunk)
        this.load(plugin)
    }

    loadByMount(mount) {
        const plugin = this.vuePlugins.find((p) => p.mount === mount)
        this.load(plugin)
    }

    loadByName(name) {
        const plugin = this.vuePlugins.find((p) => p.name === name)
        this.load(plugin)
    }

    loadByPage(page) {
        const plugins = this.vuePlugins.filter((p) => p.page === page)
        for(const plugin of plugins) {
            this.load(plugin)
        }
    }

    async load(plugin) {
        const style = await window.VueLoadPlugin(plugin)
        this.vueCssRef.push(style)
    }
}

/**
 * Model for a Vue Plugin
 */
class VuePlugin {
    key = ""
    mount = ""
    page = ""

    constructor(key, mount, page) {
        this.key = key
        this.mount = mount
        this.page = page
    }
}

 window.VuePluginRegistry = new VuePluginRegistry()
