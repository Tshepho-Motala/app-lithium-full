import DocumentGenerationProviderInterface from '@/core/interface/provider/DocumentGenerationProviderInterface'

export interface DocumentTypeInterface {
  text: string
  value: number
  typeSensitive: boolean
}
export default class DocumentGenerationMock implements DocumentGenerationProviderInterface {
  generateCsv(): Promise<void> {
    return new Promise((res, rej) => {
      setTimeout(() => {
        res()
      }, 1500)
    })
  }

  loadDocuments(): Promise<any> {
    return new Promise((res, rej) => {
      setTimeout(() => {
        res(this.docs.data)
      }, 1500)
    })
  }

  data = {
    domain: 'livescore_uk'
  }

  docs = {
    data: [
      {
        fileName: 'passport_1.jpg',
        // fileType: 'image/jpeg',
        // fileBase64: 'data:image/jpeg;base64,/Empty-file',
        // fileSize: 0,
        // typeId: 1,
        // authorServiceName: 'user-document-external',
        // functionName: 'PersonalIdentification',
        id: 41,
        reviewStatus: 'VALID',
        sensitive: true,
        typeId: 1,
        typeIcon: 'https://picsum.photos/350/165?random',
        documentFileId: 1
      },
      {
        fileName: 'a-very-long___file-name-which-shoul-visible-on-hover.jpg',
        fileType: '',
        fileBase64: '',
        fileSize: 0,
        typeId: 3,
        archived: false,
        authorServiceName: 'user-document-external',
        deleted: false,
        functionName: 'PersonalIdentification',
        id: 42,
        uploadDate: 1623307104405,
        name: 'IdentificationDocument',
        ownerGuid: 'livescore_uk/5',
        pages: [],
        reviewStatus: 'INVALID',
        uuid: 'de511004-931d-43ba-b84a-7d7430d06f1b',
        version: 0,
        type: {},
        documentFileId: 2
      },
      {
        fileName: 'drive_policy_side1.jpg',
        typeId: 1,
        archived: false,
        authorServiceName: 'user-document-external',
        deleted: false,
        functionName: 'PersonalIdentification',
        id: 4,
        uploadDate: 1623307127764,
        name: 'IdentificationDocument',
        ownerGuid: 'livescore_uk/5',
        pages: [],
        reviewStatus: 'WAITING',
        uuid: 'de511004-931d-43ba-b84a-7d7430d06f1b',
        version: 0,
        typeIcon: '',
        documentFileId: 3
      },
      {
        authorServiceName: 'user-document-external',
        deleted: false,
        functionName: 'ResidentialLocale',
        id: 5,
        uploadDate: 1623307149101,
        name: 'ProofOfResidence',
        ownerGuid: 'livescore_uk/5',
        pages: [],
        reviewStatus: 'WAITING',
        uuid: '708e47b4-fbf4-4413-9763-0b01dca7068d',
        version: 0,
        sensitive: true,
        typeId: 1,
        typeIcon: 'https://picsum.photos/350/165?random',
        documentFileId: 4
      },
      {
        fileName: 'temp.bmp',
        typeId: 1,
        archived: false,
        authorServiceName: 'user-document-external',
        deleted: false,
        functionName: 'TEST',
        id: 6,
        uploadDate: 1622809219000,
        name: 'T',
        ownerGuid: 'livescore_uk/5',
        pages: [1],
        reviewStatus: 'HISTORIC',
        uuid: 'f048e54c-1c19-4149-8302-d3bf9fb943c5',
        version: 1,
        typeIcon: 'https://picsum.photos/350/165?random',
        documentFileId: 5
      },
      {
        fileName: 'bank-account.jpg',
        typeId: 1,
        archived: false,
        authorServiceName: 'user-document-internal',
        deleted: false,
        functionName: 'UTTTTT',
        id: 7,
        uploadDate: 1622809245000,
        name: 'IT',
        ownerGuid: 'livescore_uk/5',
        pages: [1],
        reviewStatus: 'invalid',
        uuid: '015afec5-8c41-4939-a05e-65244205d4b1',
        version: 1,
        typeIcon: 'https://picsum.photos/350/165?random',
        documentFileId: 6
      },
      {
        fileName: 'passport_1.jpg',
        typeId: 1,
        authorServiceName: 'user-document-external',
        functionName: 'PersonalIdentification',
        id: 410,
        uploadDate: 1623307080854,
        reviewStatus: 'VALID',
        sensitive: true,
        typeIcon: 'https://picsum.photos/350/165?random',
        documentFileId: 7
      },
      {
        fileName: 'a-very-long___file-name-which-shoul-visible-on-hover.jpg',
        typeId: 1,
        archived: false,
        authorServiceName: 'user-document-external',
        deleted: false,
        functionName: 'PersonalIdentification',
        id: 420,
        uploadDate: 1623307104405,
        name: 'IdentificationDocument',
        ownerGuid: 'livescore_uk/5',
        pages: [],
        reviewStatus: 'INVALID',
        uuid: 'de511004-931d-43ba-b84a-7d7430d06f1b',
        version: 0,
        typeIcon: 'https://picsum.photos/350/165?random',
        documentFileId: 8
      },
      {
        fileName: 'drive_policy_side1.jpg',
        typeId: 1,
        archived: false,
        authorServiceName: 'user-document-external',
        deleted: false,
        functionName: 'PersonalIdentification',
        id: 40,
        uploadDate: 1623307127764,
        name: 'IdentificationDocument',
        ownerGuid: 'livescore_uk/5',
        pages: [],
        reviewStatus: 'WAITING',
        uuid: 'de511004-931d-43ba-b84a-7d7430d06f1b',
        version: 0,
        typeIcon: 'https://picsum.photos/350/165?random',
        documentFileId: 9
      },
      {
        fileName: 'drive_policy_side2.jpg',
        typeId: 1,
        authorServiceName: 'user-document-external',
        deleted: false,
        functionName: 'ResidentialLocale',
        id: 50,
        uploadDate: 1623307149101,
        name: 'ProofOfResidence',
        ownerGuid: 'livescore_uk/5',
        pages: [],
        reviewStatus: 'WAITING',
        uuid: '708e47b4-fbf4-4413-9763-0b01dca7068d',
        version: 0,
        sensitive: true,
        typeIcon: 'https://picsum.photos/350/165?random',
        documentFileId: 10
      },
      {
        fileName: 'temp.bmp',
        typeId: 1,
        archived: false,
        authorServiceName: 'user-document-external',
        deleted: false,
        functionName: 'TEST',
        id: 60,
        uploadDate: 1622809219000,
        name: 'T',
        ownerGuid: 'livescore_uk/5',
        pages: [1],
        reviewStatus: 'HISTORIC',
        uuid: 'f048e54c-1c19-4149-8302-d3bf9fb943c5',
        version: 1,
        typeIcon: 'https://picsum.photos/350/165?random',
        documentFileId: 11
      },
      {
        fileName: 'bank-account.jpg',
        typeId: 1,
        archived: false,
        authorServiceName: 'user-document-internal',
        deleted: false,
        functionName: 'UTTTTT',
        id: 70,
        uploadDate: 1622809245000,
        name: 'IT',
        ownerGuid: 'livescore_uk/5',
        pages: [1],
        reviewStatus: 'invalid',
        uuid: '015afec5-8c41-4939-a05e-65244205d4b1',
        version: 1,
        typeName: 'passport',
        typeIcon: 'https://picsum.photos/350/165?random',
        documentFileId: 12
      }
    ],
    data2: null,
    message: null,
    status: 0,
    successful: true
  }

  docTypes = [
    {
      id: 1,
      purpose: 'Internal',
      type: 'Passport',
      enabled: true,
      iconBase64: 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAA',
      iconName: 'pasport-icon.jpg',
      iconType: 'image/jpeg',
      iconSize: 1313123,
      modifiedDate: 1623307080854,
      typeSensitive: true // Assumption is that this is the new data property coming from the API
    },
    { id: 2, modifiedDate: 1623307104405, purpose: 'Internal', type: 'ID Card-front', iconBase64: '', enabled: true, typeSensitive: false },
    { id: 3, modifiedDate: 1623307127764, purpose: 'Internal', type: 'ID-Card-back', iconBase64: '', enabled: true, typeSensitive: false },
    { id: 4, modifiedDate: 1623307149101, purpose: 'External', type: 'ProofOfAddress', iconBase64: '', enabled: false, typeSensitive: true },
    { id: 6, modifiedDate: 1623307179941, purpose: 'Internal', type: 'Other', iconBase64: '', enabled: true, typeSensitive: false }
  ]

  loadDocumentTypes(): Promise<any> {
    return new Promise((res, rej) => {
      setTimeout(() => {
        res(this.docTypes)
      }, 1500)
    })
  }

  saveDocumentType(documentType: any): Promise<any> {
    return new Promise((res, rej) => {
      setTimeout(() => {
        documentType.modifiedDate = Date.now()
        if (documentType.id === undefined) {
          let maxId = 1
          if (this.docTypes.length > 0) {
            maxId = this.docTypes.sort((a: any, b: any) => b.id - a.id)[0].id
          }
          documentType.id = maxId + 1
          console.log('Added id for doc type: ' + documentType.id)
        }
        res(documentType)
      }, 1500)
    })
  }

  loadAvailableDocumentTypes(internalOnly: boolean): Promise<DocumentTypeInterface[]> {
    return new Promise((res, rej) => {
      setTimeout(() => {
        const mapped = this.docTypes.map((item) => ({
          text: item.type + (internalOnly ? ' (Internal)' : ''),
          value: item.id,
          typeSensitive: item.typeSensitive
        }))
        res(mapped)
      }, 500)
    })
  }

  loadAvailableReviewReasons(): Promise<any> {
    return new Promise((res, rej) => {
      setTimeout(() => {
        const avalableReasons = [
          { text: 'Cropped', value: 'Cropped' },
          { text: 'Doctored', value: 'Doctored' },
          { text: 'Falsified Document ', value: 'Falsified Document ' },
          { text: 'Personal Details Mismatch', value: 'Personal Details Mismatch' },
          { text: 'Unreadable - blurred', value: 'Unreadable - blurred' },
          { text: 'Unreadable - low resolution', value: 'Unreadable - low resolution' },
          { text: 'Unreadable - obscured data', value: 'Unreadable - obscured data' },
          { text: 'Out of date', value: 'Out of date' },
          { text: 'Incorrect Document', value: 'Incorrect Document' },
          { text: 'Awaiting Back', value: 'Awaiting Back' },
          { text: 'ID verified - Needs Address', value: 'ID verified - Needs Address' },
          { text: 'ID & Address Verified', value: 'ID & Address Verified' },
          { text: 'Other', value: 'Other' }
        ]
        res(avalableReasons)
      }, 500)
    })
  }

  updateDocument(document: any): Promise<void> {
    return new Promise((res, rej) => {
      setTimeout(() => {
        document.uploadDate = Date.now()
        if (document.id === undefined) {
          let maxId = 1
          if (this.docs.data.length > 0) {
            maxId = this.docs.data.sort((a: any, b: any) => b.id - a.id)[0].id
          }
          document.id = maxId + 1
          console.log('Added id for doc : ' + document.id)
        }
        console.log('Saved document ', document)

        res()
      }, 1500)
    })
  }

  uploadedFiles: any = {}

  uploadDocument(file: any, document: any): Promise<any> {
    return new Promise((res, rej) => {
      setTimeout(() => {
        document.uploadDate = Date.now()
        if (document.id === undefined) {
          let maxId = 1
          if (this.docs.data.length > 0) {
            maxId = this.docs.data.sort((a: any, b: any) => b.id - a.id)[0].id
          }
          document.id = maxId + 1
          console.log('Added id for doc : ' + document.id)
        }

        if (file != null && file.name) {
          const reader = new FileReader()
          reader.onload = (e) => {
            if (e !== null && e.target !== null) {
              document.fileName = file.name
              document.documentFileId = document.id
              this.uploadedFiles[document.id] = {}
              this.uploadedFiles[document.id].type = file.type
              this.uploadedFiles[document.id].base64 = e.target.result
            }
          }
          reader.readAsDataURL(file)
        }

        console.log('Saved document ', document)
        console.log('Saved file ', this.uploadedFiles)

        res({ data: document })
      }, 1500)
    })
  }

  deleteDocument(documentId: any): Promise<void> {
    return new Promise((res, rej) => {
      setTimeout(() => {
        res()
      }, 1500)
    })
  }

  loadDocumentFile(documentFileId: any): Promise<any> {
    return new Promise((res, rej) => {
      setTimeout(() => {
        console.log('Retrieving file ', documentFileId)
        console.log('Uploaded files ', this.uploadedFiles)
        const documentFile = {
          id: documentFileId,
          file: {
            id: 111,
            name: 'passport_1.jpg',
            mimeType: this.uploadedFiles[documentFileId].type,
            data: '/Empty-file',
            base64: this.uploadedFiles[documentFileId].base64,
            size: 0,
            md5Hash: ''
          },
          deleted: false,
          uploadDate: 12345,
          documentPage: 0
        }
        res(documentFile)
      }, 1500)
    })
  }

  closeUploadDialog(): void {
    return
  }
}
