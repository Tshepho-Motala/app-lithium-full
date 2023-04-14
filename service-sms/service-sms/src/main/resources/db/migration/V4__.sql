UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%emailAddress%', '%user.emailAddress%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%emailAddress%', '%user.emailAddress%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%emailAddress%', '%user.emailAddress%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%emailAddress%', '%user.emailAddress%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%username%', '%user.userName%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%username%', '%user.userName%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%username%', '%user.userName%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%username%', '%user.userName%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%email%', '%user.emailAddress%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%email%', '%user.emailAddress%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%email%', '%user.emailAddress%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%email%', '%user.emailAddress%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%playerId%', '%user.guid%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%playerId%', '%user.guid%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%playerId%', '%user.guid%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%playerId%', '%user.guid%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%playerLink%', '%user.playerLink%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%playerLink%', '%user.playerLink%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%playerLink%', '%user.playerLink%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%playerLink%', '%user.playerLink%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%firstName%', '%user.firstName%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%firstName%', '%user.firstName%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%firstName%', '%user.firstName%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%firstName%', '%user.firstName%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%lastName%', '%user.lastName%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%lastName%', '%user.lastName%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%lastName%', '%user.lastName%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%lastName%', '%user.lastName%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%domainName%', '%domain.name%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%domainName%', '%domain.name%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%domainName%', '%domain.name%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%domainName%', '%domain.name%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%domainUrl%', '%domain.url%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%domainUrl%', '%domain.url%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%domainUrl%', '%domain.url%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%domainUrl%', '%domain.url%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%domainSupportEmail%', '%domain.supportEmail%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%domainSupportEmail%', '%domain.supportEmail%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%domainSupportEmail%', '%domain.supportEmail%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%domainSupportEmail%', '%domain.supportEmail%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%transactionType%', '%cashier.transactionType%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%transactionType%', '%cashier.transactionType%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%transactionType%', '%cashier.transactionType%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%transactionType%', '%cashier.transactionType%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%transactionId%', '%cashier.transactionId%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%transactionId%', '%cashier.transactionId%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%transactionId%', '%cashier.transactionId%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%transactionId%', '%cashier.transactionId%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%amount%', '%cashier.amount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%amount%', '%cashier.amount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%amount%', '%cashier.amount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%amount%', '%cashier.amount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%processorMethod%', '%cashier.processorMethod%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%processorMethod%', '%cashier.processorMethod%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%processorMethod%', '%cashier.processorMethod%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%processorMethod%', '%cashier.processorMethod%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%processorReference%', '%cashier.processorReference%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%processorReference%', '%cashier.processorReference%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%processorReference%', '%cashier.processorReference%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%processorReference%', '%cashier.processorReference%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%processorResponse%', '%cashier.processorResponse%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%processorResponse%', '%cashier.processorResponse%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%processorResponse%', '%cashier.processorResponse%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%processorResponse%', '%cashier.processorResponse%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%request%', '%cashier.request%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%request%', '%cashier.request%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%request%', '%cashier.request%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%request%', '%cashier.request%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%response%', '%cashier.response%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%response%', '%cashier.response%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%response%', '%cashier.response%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%response%', '%cashier.response%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%billingDescriptor%', '%cashier.billingDescriptor%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%billingDescriptor%', '%cashier.billingDescriptor%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%billingDescriptor%', '%cashier.billingDescriptor%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%billingDescriptor%', '%cashier.billingDescriptor%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%transactionFee%', '%cashier.transactionFee%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%transactionFee%', '%cashier.transactionFee%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%transactionFee%', '%cashier.transactionFee%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%transactionFee%', '%cashier.transactionFee%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%brand%', '%domain.name%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%brand%', '%domain.name%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%brand%', '%domain.name%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%brand%', '%domain.name%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%accountStatus%', '%user.accountStatus%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%accountStatus%', '%user.accountStatus%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%accountStatus%', '%user.accountStatus%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%accountStatus%', '%user.accountStatus%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%kycStatus%', '%user.verificationStatus%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%kycStatus%', '%user.verificationStatus%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%kycStatus%', '%user.verificationStatus%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%kycStatus%', '%user.verificationStatus%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%ageVerified%', '%user.ageVerified%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%ageVerified%', '%user.ageVerified%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%ageVerified%', '%user.ageVerified%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%ageVerified%', '%user.ageVerified%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%addressVerified%', '%user.addressVerified%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%addressVerified%', '%user.addressVerified%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%addressVerified%', '%user.addressVerified%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%addressVerified%', '%user.addressVerified%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%fileName1%', '%document.fileName1%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%fileName1%', '%document.fileName1%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%fileName1%', '%document.fileName1%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%fileName1%', '%document.fileName1%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%fileLink1%', '%document.fileLink1%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%fileLink1%', '%document.fileLink1%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%fileLink1%', '%document.fileLink1%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%fileLink1%', '%document.fileLink1%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%fileTimestamp1%', '%document.fileTimestamp1%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%fileTimestamp1%', '%document.fileTimestamp1%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%fileTimestamp1%', '%document.fileTimestamp1%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%fileTimestamp1%', '%document.fileTimestamp1%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%fileName2%', '%document.fileName2%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%fileName2%', '%document.fileName2%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%fileName2%', '%document.fileName2%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%fileName2%', '%document.fileName2%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%fileLink2%', '%document.fileLink2%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%fileLink2%', '%document.fileLink2%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%fileLink2%', '%document.fileLink2%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%fileLink2%', '%document.fileLink2%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%fileTimestamp2%', '%document.fileTimestamp2%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%fileTimestamp2%', '%document.fileTimestamp2%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%fileTimestamp2%', '%document.fileTimestamp2%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%fileTimestamp2%', '%document.fileTimestamp2%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%documentType%', '%document.type%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%documentType%', '%document.type%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%documentType%', '%document.type%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%documentType%', '%document.type%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%notificationMethod%', '%cashier.notificationMethod%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%notificationMethod%', '%cashier.notificationMethod%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%notificationMethod%', '%cashier.notificationMethod%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%notificationMethod%', '%cashier.notificationMethod%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%createdDate%', '%user.createdDate%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%createdDate%', '%user.createdDate%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%createdDate%', '%user.createdDate%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%createdDate%', '%user.createdDate%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%dateOfBirth%', '%user.dateOfBirth%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%dateOfBirth%', '%user.dateOfBirth%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%dateOfBirth%', '%user.dateOfBirth%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%dateOfBirth%', '%user.dateOfBirth%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%playThroughCents%', '%casino.playThroughCents%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%playThroughCents%', '%casino.playThroughCents%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%playThroughCents%', '%casino.playThroughCents%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%playThroughCents%', '%casino.playThroughCents%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%playThroughRequiredCents%', '%casino.playThroughRequiredCents%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%playThroughRequiredCents%', '%casino.playThroughRequiredCents%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%playThroughRequiredCents%', '%casino.playThroughRequiredCents%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%playThroughRequiredCents%', '%casino.playThroughRequiredCents%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%triggerAmount%', '%casino.triggerAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%triggerAmount%', '%casino.triggerAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%triggerAmount%', '%casino.triggerAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%triggerAmount%', '%casino.triggerAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%bonusAmount%', '%casino.bonusAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%bonusAmount%', '%casino.bonusAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%bonusAmount%', '%casino.bonusAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%bonusAmount%', '%casino.bonusAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%bonusPercentage%', '%casino.bonusPercentage%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%bonusPercentage%', '%casino.bonusPercentage%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%bonusPercentage%', '%casino.bonusPercentage%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%bonusPercentage%', '%casino.bonusPercentage%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%bonusCode%', '%casino.bonusCode%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%bonusCode%', '%casino.bonusCode%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%bonusCode%', '%casino.bonusCode%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%bonusCode%', '%casino.bonusCode%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%bonusName%', '%casino.bonusName%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%bonusName%', '%casino.bonusName%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%bonusName%', '%casino.bonusName%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%bonusName%', '%casino.bonusName%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%name%', '%report.name%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%name%', '%report.name%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%name%', '%report.name%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%name%', '%report.name%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%startedOn%', '%report.startedOn%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%startedOn%', '%report.startedOn%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%startedOn%', '%report.startedOn%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%startedOn%', '%report.startedOn%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%completedOn%', '%report.completedOn%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%completedOn%', '%report.completedOn%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%completedOn%', '%report.completedOn%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%completedOn%', '%report.completedOn%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%startedBy%', '%report.startedBy%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%startedBy%', '%report.startedBy%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%startedBy%', '%report.startedBy%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%startedBy%', '%report.startedBy%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%totalRecords%', '%report.totalRecords%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%totalRecords%', '%report.totalRecords%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%totalRecords%', '%report.totalRecords%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%totalRecords%', '%report.totalRecords%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%processedRecords%', '%report.processedRecords%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%processedRecords%', '%report.processedRecords%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%processedRecords%', '%report.processedRecords%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%processedRecords%', '%report.processedRecords%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%currentBalance%', '%report.currentBalance%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%currentBalance%', '%report.currentBalance%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%currentBalance%', '%report.currentBalance%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%currentBalance%', '%report.currentBalance%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%currentBalanceCasinoBonus%', '%report.currentBalanceCasinoBonus%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%currentBalanceCasinoBonus%', '%report.currentBalanceCasinoBonus%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%currentBalanceCasinoBonus%', '%report.currentBalanceCasinoBonus%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%currentBalanceCasinoBonus%', '%report.currentBalanceCasinoBonus%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%currentBalanceCasinoBonusPending%', '%report.currentBalanceCasinoBonusPending%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%currentBalanceCasinoBonusPending%', '%report.currentBalanceCasinoBonusPending%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%currentBalanceCasinoBonusPending%', '%report.currentBalanceCasinoBonusPending%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%currentBalanceCasinoBonusPending%', '%report.currentBalanceCasinoBonusPending%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%periodOpeningBalance%', '%report.periodOpeningBalance%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%periodOpeningBalance%', '%report.periodOpeningBalance%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%periodOpeningBalance%', '%report.periodOpeningBalance%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%periodOpeningBalance%', '%report.periodOpeningBalance%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%periodClosingBalance%', '%report.periodClosingBalance%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%periodClosingBalance%', '%report.periodClosingBalance%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%periodClosingBalance%', '%report.periodClosingBalance%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%periodClosingBalance%', '%report.periodClosingBalance%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%periodOpeningBalanceCasinoBonus%', '%report.periodOpeningBalanceCasinoBonus%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%periodOpeningBalanceCasinoBonus%', '%report.periodOpeningBalanceCasinoBonus%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%periodOpeningBalanceCasinoBonus%', '%report.periodOpeningBalanceCasinoBonus%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%periodOpeningBalanceCasinoBonus%', '%report.periodOpeningBalanceCasinoBonus%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%periodClosingBalanceCasinoBonus%', '%report.periodClosingBalanceCasinoBonus%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%periodClosingBalanceCasinoBonus%', '%report.periodClosingBalanceCasinoBonus%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%periodClosingBalanceCasinoBonus%', '%report.periodClosingBalanceCasinoBonus%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%periodClosingBalanceCasinoBonus%', '%report.periodClosingBalanceCasinoBonus%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%periodOpeningBalanceCasinoBonusPending%', '%report.periodOpeningBalanceCasinoBonusPending%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%periodOpeningBalanceCasinoBonusPending%', '%report.periodOpeningBalanceCasinoBonusPending%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%periodOpeningBalanceCasinoBonusPending%', '%report.periodOpeningBalanceCasinoBonusPending%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%periodOpeningBalanceCasinoBonusPending%', '%report.periodOpeningBalanceCasinoBonusPending%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%periodClosingBalanceCasinoBonusPending%', '%report.periodClosingBalanceCasinoBonusPending%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%periodClosingBalanceCasinoBonusPending%', '%report.periodClosingBalanceCasinoBonusPending%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%periodClosingBalanceCasinoBonusPending%', '%report.periodClosingBalanceCasinoBonusPending%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%periodClosingBalanceCasinoBonusPending%', '%report.periodClosingBalanceCasinoBonusPending%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%depositAmount%', '%report.depositAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%depositAmount%', '%report.depositAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%depositAmount%', '%report.depositAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%depositAmount%', '%report.depositAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%depositCount%', '%report.depositCount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%depositCount%', '%report.depositCount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%depositCount%', '%report.depositCount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%depositCount%', '%report.depositCount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%payoutAmount%', '%report.payoutAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%payoutAmount%', '%report.payoutAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%payoutAmount%', '%report.payoutAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%payoutAmount%', '%report.payoutAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%payoutCount%', '%report.payoutCount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%payoutCount%', '%report.payoutCount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%payoutCount%', '%report.payoutCount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%payoutCount%', '%report.payoutCount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%balanceAdjustAmount%', '%report.balanceAdjustAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%balanceAdjustAmount%', '%report.balanceAdjustAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%balanceAdjustAmount%', '%report.balanceAdjustAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%balanceAdjustAmount%', '%report.balanceAdjustAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%balanceAdjustCount%', '%report.balanceAdjustCount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%balanceAdjustCount%', '%report.balanceAdjustCount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%balanceAdjustCount%', '%report.balanceAdjustCount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%balanceAdjustCount%', '%report.balanceAdjustCount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBetAmount%', '%report.casinoBetAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBetAmount%', '%report.casinoBetAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBetAmount%', '%report.casinoBetAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBetAmount%', '%report.casinoBetAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBetCount%', '%report.casinoBetCount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBetCount%', '%report.casinoBetCount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBetCount%', '%report.casinoBetCount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBetCount%', '%report.casinoBetCount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoWinAmount%', '%report.casinoWinAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoWinAmount%', '%report.casinoWinAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoWinAmount%', '%report.casinoWinAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoWinAmount%', '%report.casinoWinAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoWinCount%', '%report.casinoWinCount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoWinCount%', '%report.casinoWinCount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoWinCount%', '%report.casinoWinCount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoWinCount%', '%report.casinoWinCount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoNetAmount%', '%report.casinoNetAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoNetAmount%', '%report.casinoNetAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoNetAmount%', '%report.casinoNetAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoNetAmount%', '%report.casinoNetAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusBetAmount%', '%report.casinoBonusBetAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusBetAmount%', '%report.casinoBonusBetAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusBetAmount%', '%report.casinoBonusBetAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusBetAmount%', '%report.casinoBonusBetAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusBetCount%', '%report.casinoBonusBetCount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusBetCount%', '%report.casinoBonusBetCount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusBetCount%', '%report.casinoBonusBetCount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusBetCount%', '%report.casinoBonusBetCount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusWinAmount%', '%report.casinoBonusWinAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusWinAmount%', '%report.casinoBonusWinAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusWinAmount%', '%report.casinoBonusWinAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusWinAmount%', '%report.casinoBonusWinAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusWinCount%', '%report.casinoBonusWinCount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusWinCount%', '%report.casinoBonusWinCount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusWinCount%', '%report.casinoBonusWinCount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusWinCount%', '%report.casinoBonusWinCount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusNetAmount%', '%report.casinoBonusNetAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusNetAmount%', '%report.casinoBonusNetAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusNetAmount%', '%report.casinoBonusNetAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusNetAmount%', '%report.casinoBonusNetAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusPendingAmount%', '%report.casinoBonusPendingAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusPendingAmount%', '%report.casinoBonusPendingAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusPendingAmount%', '%report.casinoBonusPendingAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusPendingAmount%', '%report.casinoBonusPendingAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusTransferToBonusPendingAmount%', '%report.casinoBonusTransferToBonusPendingAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusTransferToBonusPendingAmount%', '%report.casinoBonusTransferToBonusPendingAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusTransferToBonusPendingAmount%', '%report.casinoBonusTransferToBonusPendingAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusTransferToBonusPendingAmount%', '%report.casinoBonusTransferToBonusPendingAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusTransferFromBonusPendingAmount%', '%report.casinoBonusTransferFromBonusPendingAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusTransferFromBonusPendingAmount%', '%report.casinoBonusTransferFromBonusPendingAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusTransferFromBonusPendingAmount%', '%report.casinoBonusTransferFromBonusPendingAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusTransferFromBonusPendingAmount%', '%report.casinoBonusTransferFromBonusPendingAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusPendingCancelAmount%', '%report.casinoBonusPendingCancelAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusPendingCancelAmount%', '%report.casinoBonusPendingCancelAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusPendingCancelAmount%', '%report.casinoBonusPendingCancelAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusPendingCancelAmount%', '%report.casinoBonusPendingCancelAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusPendingCount%', '%report.casinoBonusPendingCount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusPendingCount%', '%report.casinoBonusPendingCount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusPendingCount%', '%report.casinoBonusPendingCount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusPendingCount%', '%report.casinoBonusPendingCount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusActivateAmount%', '%report.casinoBonusActivateAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusActivateAmount%', '%report.casinoBonusActivateAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusActivateAmount%', '%report.casinoBonusActivateAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusActivateAmount%', '%report.casinoBonusActivateAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusTransferToBonusAmount%', '%report.casinoBonusTransferToBonusAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusTransferToBonusAmount%', '%report.casinoBonusTransferToBonusAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusTransferToBonusAmount%', '%report.casinoBonusTransferToBonusAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusTransferToBonusAmount%', '%report.casinoBonusTransferToBonusAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusTransferFromBonusAmount%', '%report.casinoBonusTransferFromBonusAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusTransferFromBonusAmount%', '%report.casinoBonusTransferFromBonusAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusTransferFromBonusAmount%', '%report.casinoBonusTransferFromBonusAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusTransferFromBonusAmount%', '%report.casinoBonusTransferFromBonusAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusCancelAmount%', '%report.casinoBonusCancelAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusCancelAmount%', '%report.casinoBonusCancelAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusCancelAmount%', '%report.casinoBonusCancelAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusCancelAmount%', '%report.casinoBonusCancelAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusExpireAmount%', '%report.casinoBonusExpireAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusExpireAmount%', '%report.casinoBonusExpireAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusExpireAmount%', '%report.casinoBonusExpireAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusExpireAmount%', '%report.casinoBonusExpireAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%casinoBonusMaxPayoutExcessAmount%', '%report.casinoBonusMaxPayoutExcessAmount%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%casinoBonusMaxPayoutExcessAmount%', '%report.casinoBonusMaxPayoutExcessAmount%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%casinoBonusMaxPayoutExcessAmount%', '%report.casinoBonusMaxPayoutExcessAmount%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%casinoBonusMaxPayoutExcessAmount%', '%report.casinoBonusMaxPayoutExcessAmount%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%OptOutEmailUrl%', '%user.optOutEmailUrl%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%OptOutEmailUrl%', '%user.optOutEmailUrl%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%OptOutEmailUrl%', '%user.optOutEmailUrl%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%OptOutEmailUrl%', '%user.optOutEmailUrl%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%to%', '%settlement.to%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%to%', '%settlement.to%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%to%', '%settlement.to%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%to%', '%settlement.to%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%period%', '%settlement.period%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%period%', '%settlement.period%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%to%', '%settlement.period%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%period%', '%settlement.period%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%playingToLevel%', '%xp.playingToLevel%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%playingToLevel%', '%xp.playingToLevel%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%playingToLevel%', '%xp.playingToLevel%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%playingToLevel%', '%xp.playingToLevel%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%progress%', '%xp.progress%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%progress%', '%xp.progress%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%progress%', '%xp.progress%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%progress%', '%xp.progress%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%bonusCode%', '%xp.bonusCode%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%bonusCode%', '%xp.bonusCode%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%bonusCode%', '%xp.bonusCode%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%bonusCode%', '%xp.bonusCode%');

UPDATE `defaultsmstemplate` SET description = REPLACE(description, '%isMilestone%', '%xp.isMilestone%');
UPDATE `defaultsmstemplate` SET text = REPLACE(text, '%isMilestone%', '%xp.isMilestone%');
UPDATE `defaultsmstemplate_placeholder` SET name = REPLACE(name, '%isMilestone%', '%xp.isMilestone%');
UPDATE `smstemplate_revision` SET text = REPLACE(text, '%isMilestone%', '%xp.isMilestone%');

