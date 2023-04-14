import { PluginRegistryInterface, PluginRegistrarInterface } from '@/core/interface/sdk/PluginInterface'
import { SideMenuItem } from '@/core/components/side-menu/SideMenuModel'

export default class PluginRegistry implements PluginRegistryInterface {
  plugins: PluginRegistrarInterface[] = []

  constructor() {
    this.registerMockPlugins()
  }

  getByUid(uid: string): PluginRegistrarInterface | false {
    return this.plugins.find((x) => x.uid === uid) || false
  }

  /**
   * DEPRECATED! Use routes instead
   */
  private registerMockPlugins() {
    // DEMO PAGES
    this.routePlugin(import('@/demo/PermissionDemo.vue'), '/demo-permission', 'Demo: Permissions')
    this.routePlugin(import('@/demo/TranslateDemo.vue'), '/demo-translation', 'Demo: Translations')
    this.routePlugin(import('@/demo/ExternalCall.vue'), '/demo-external-call', 'Demo: External Call')

    // Documents Upload v2 Page
    // this.routePlugin(import('@/plugin/documents/DocumentsUploadV2Tab.vue'), '/docs-upload', 'Plugin: Documents upload v2') // TODO: Figure out where this went to
    this.routePlugin(import('@/plugin/documents/ManageDocumentTypesTab.vue'), '/doc-types', 'Plugin: Document types')
    this.routePlugin(import('@/plugin/documents/DocumentsTab.vue'), '/docs', 'Plugin: Documents')
    this.routePlugin(import('@/plugin/documents/DocumentUploadQuickAction.vue'), '/doc-upload-quick-action', 'Plugin: Document Upload - Quick action')
    this.routePlugin(import('@/plugin/documents/ManageDocumentTypesTab.vue'), '/doc-types', 'Plugin: Document types')
    // Last X transactions table
    this.routePlugin(import('@/plugin/cashier/LastXCashierTransactions.vue'), '/last-x-transactions', 'Plugin: Last X Transactions')

    // Bank Account Lookup
    this.routePlugin(import('@/plugin/cashier/BankAccountLookup.vue'), '/bank-account-lookup/page', 'Plugin: Bank Account Lookup')

    //CMS
    this.routePlugin(import('@/plugin/cms/images/Images.vue'), '/cms-casino-images', 'Plugin: Casino Images')
    this.routePlugin(import('@/plugin/cms/webassets/Assets.vue'), '/cms-asset-upload', 'Plugin: CMS Assets')

    //CSV EXport
    this.routePlugin(import('@/plugin/csv-export/ButtonExport.vue'), '/cms-export', 'Plugin: CSV Export')

    //Player Protection
    this.routePlugin(import('@/plugin/player-protection/PlayerProtection.vue'), '/player-protection', 'Plugin: Player-protection')
  }
  
  sidebarPartent(sidebarTitle: string) {
    const plugin = this.basePlugin(null)
    plugin.sideMenu = new SideMenuItem(sidebarTitle)

    this.plugins.push(plugin)
    return plugin
  }

  routePlugin(importer: Promise<any>, routePath: string, sidebarTitle?: string, sidebarParent?: PluginRegistrarInterface) {
    const plugin = this.basePlugin(importer)
    const routeName = routePath.substr(1, routePath.length)

    plugin.route = {
      path: routePath,
      name: routeName
    }

    if (sidebarTitle) {
      plugin.sideMenu = new SideMenuItem(sidebarTitle, routeName)
    }

    if (sidebarParent) {
      sidebarParent.sideMenu?.addChildPlugin(plugin, sidebarTitle!)
    }

    this.plugins.push(plugin)

    return plugin
  }

  private basePlugin(importer: Promise<any> | null): PluginRegistrarInterface {
    return {
      uid: 'plugin-uid-' + this.plugins.length,
      importer: () => importer
    }
  }
}
