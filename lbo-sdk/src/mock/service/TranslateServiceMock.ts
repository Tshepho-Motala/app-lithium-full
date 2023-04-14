import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface"

export default class TranslateServiceMock implements TranslateServiceInterface {
  translations: Map<string, string> = new Map(
    [
      ['UI_NETWORK_ADMIN.MAIL.TEMPLATES.ADD.TITLE', 'Add Template'],

      ['UI_NETWORK_ADMIN.CASINO.IMAGES.HEADER.TITLE', 'CMS Images | LBO'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.TITLE', 'CMS Images'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.DESCRIPTION', 'Manage cms images for casino'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.BANNER.TITLE', 'Add Banner Image'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.TILE.TITLE', 'Add Tile Image'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.BUTTON.ADD', 'Add Image'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FIELDS.NAME.NAME', 'Image'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FIELDS.URL.NAME', 'Image Url'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FIELDS.SIZE.NAME', 'File Size'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FIELDS.UPLOADED_DATE.NAME', 'Date Uploaded'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.BANNER.NAME', 'Banner Name'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.TILE.NAME', 'Game Key'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.BUTTON', 'Upload'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.BANNER.SUCCESS_MESSAGE', 'Banner image was upload successfully'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.TILE.SUCCESS_MESSAGE', 'Tile image was upload successfully'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.BANNER.ERROR_MESSAGE', 'Failed to upload banner image'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.INVALID.ERROR_MESSAGE', 'Failed to upload file, only images of type jpg, jpeg, png and svg are allowed.'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.TILE.ERROR_MESSAGE', 'Failed to upload banner image'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.TILE.UNIQUE', 'Game key needs to be unique for a given image size'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.BANNER.UNIQUE', 'Banner name needs to be unique for a given image size'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.TABS.TILE', 'Tiles'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.TABS.BANNER', 'Banners'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.BUTTON.DELETE', 'Delete'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.TITLE', 'Please confirm deletion'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.PROMPT', 'Click cancel to go back, or click confirm to permanently delete this item'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.SUCCESS_MESSAGE', 'Image was removed sucessfully'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.ERROR_MESSAGE', 'Failed to remove image'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.CONFIRM', 'Confirm'],
      ['UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.CANCEL', 'Cancel'],

      // CMS

      // CMS JACKPOT WIDGET LAYOUT CONFIG
      ['UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.HEADER.TITLE', 'Jackpot Details'],
      ['UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.JACKPOT_LOGO.LABEL', 'Jackpot Logo'],
      ['UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.DESCRIPTION.LABEL', 'Description'],
      ['UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.PROGRESSIVES.LABEL', 'Progressives'],
      ['UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.PROGRESSIVES.VALIDATION.MIN_ERROR_MESSAGE', 'You have to select at least one progressive item'],
      ['UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.PROGRESSIVES.VALIDATION.MAX_ERROR_MESSAGE', 'You have to select a minimum of 1 progressive item and a maximum of 3 progressive items'],
      ['UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.WIDGET_LINK.LABEL', 'Widget Link'],
      ['UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.WIDGET_LINK_TITLE.LABEL', 'Widget Link Title'],

      // CMS PROGRESSIVE LINK TITLE
        ['UI_NETWORK_ADMIN.CMS.PROGRESSIVE_ITEM_EDITOR.HEADER.TITLE', 'Progressive Details Editor'],
        ['UI_NETWORK_ADMIN.CMS.PROGRESSIVE_ITEM_EDITOR.FIELDS.DESCRIPTION.LABEL', 'Progressive ID'],
        ['UI_NETWORK_ADMIN.CMS.PROGRESSIVE_ITEM_EDITOR.FIELDS.TITLE.LABEL', 'Title'],
        ['UI_NETWORK_ADMIN.CMS.PROGRESSIVE_ITEM_EDITOR.FIELDS.DESCRIPTION.LABEL', 'Description'],


      //UPLOADING WEB ASSETS
      ['UI_NETWORK_ADMIN.CMS.ASSETS.TITLE', 'Web Assets'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.DESCRIPTION', 'Manage web images'],

      ['UI_NETWORK_ADMIN.CMS.ASSETS.FORM.TITLE', 'Upload Asset'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.BUTTON.ADD', 'Add'],

      ['UI_NETWORK_ADMIN.CMS.ASSETS.ADD.SUCCESS_MESSAGE', 'Asset was added sucessfully'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.ADD.ERROR_MESSAGE', 'Failed to add asset'],

      //Table fields
      ['UI_NETWORK_ADMIN.CMS.ASSETS.FIELDS.NAME.NAME', 'Name'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.FIELDS.URL.NAME',  'Url'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.FIELDS.SIZE.NAME', 'File Size'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.FIELDS.UPLOADED_DATE.NAME', 'Date Uploaded'],
     
      //Popup Form Button
      ['UI_NETWORK_ADMIN.CMS.ASSETS.FORM.BUTTON.UPLOAD', 'Upload'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.FORM.BUTTON.CANCEL', 'Cancel'],
     
      //TABS
      ['UI_NETWORK_ADMIN.CMS.ASSETS.TABS.STYLE', 'Styles'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.TABS.FONT', 'Fonts'],

      //CONFIRMATION MODAL
      ['UI_NETWORK_ADMIN.CMS.ASSETS.BUTTON.DELETE', 'Delete'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.TITLE', 'Please confirm deletion'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.PROMPT', 'Click cancel to go back, or click confirm to permanently delete this item'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.SUCCESS_MESSAGE', 'Asset was removed sucessfully'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.ERROR_MESSAGE', 'Failed to remove asset'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.CONFIRM', 'Confirm'],
      ['UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.CANCEL', 'Cancel'],

      ['UI_NETWORK_ADMIN.CMS.CHANNEL_SELECTOR.OUTPUT.TITLE', 'Channel'],
      ['UI_NETWORK_ADMIN.CMS.CHANNEL_SELECTOR.OUTPUT.SELECT_CHANNEL', 'Select a channel to configure'],
      ['UI_NETWORK_ADMIN.CMS.GLOBAL.OUTPUT.SELECT_LOBBY_MESSAGE', 'Please select a lobby first'],

      ['UI_NETWORK_ADMIN.CMS.OUTPUT.DOMAIN_SELECTOR.TITLE', 'Select a Domain'],
      ['UI_NETWORK_ADMIN.CMS.OUTPUT.DOMAIN_SELECTOR.DESCRIPTION', 'To manage games and promotional content within the Casino Lobby pages'],

      //CSV
      ['UI_NETWORK_ADMIN.CSVEXPORT.BUTTON.EXPORT', 'Export'],
      ['UI_NETWORK_ADMIN.CSVEXPORT.BUTTON.DOWNLOAD', 'Download'],

      // DOMAIN
      ['UI_NETWORK_ADMIN.DOMAIN_SELECTOR.OUTPUT.SELECT_DOMAIN', 'Select a Domain'],
      ['UI_NETWORK_ADMIN.DOMAIN_SELECTOR.OUTPUT.SELECTED_DOMAIN_HINT', 'Click the reset button to select a different domain'],
    ])

  instant(key: string) {
    return this.translations.get(key) || key || ''
  }

  isReady() {
    return true
  }
}
