UPDATE `default_email_template` SET body = REPLACE(body, '%emailAddress%', '%user.emailAddress%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%emailAddress%', '%user.emailAddress%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%emailAddress%', '%user.emailAddress%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%emailAddress%', '%user.emailAddress%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%emailAddress%', '%user.emailAddress%');

UPDATE `default_email_template` SET body = REPLACE(body, '%username%', '%user.userName%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%username%', '%user.userName%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%username%', '%user.userName%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%username%', '%user.userName%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%username%', '%user.userName%');

UPDATE `default_email_template` SET body = REPLACE(body, '%email%', '%user.emailAddress%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%email%', '%user.emailAddress%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%email%', '%user.emailAddress%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%email%', '%user.emailAddress%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%email%', '%user.emailAddress%');

UPDATE `default_email_template` SET body = REPLACE(body, '%playerId%', '%user.guid%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%playerId%', '%user.guid%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%playerId%', '%user.guid%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%playerId%', '%user.guid%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%playerId%', '%user.guid%');

UPDATE `default_email_template` SET body = REPLACE(body, '%playerLink%', '%user.playerLink%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%playerLink%', '%user.playerLink%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%playerLink%', '%user.playerLink%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%playerLink%', '%user.playerLink%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%playerLink%', '%user.playerLink%');

UPDATE `default_email_template` SET body = REPLACE(body, '%firstName%', '%user.firstName%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%firstName%', '%user.firstName%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%firstName%', '%user.firstName%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%firstName%', '%user.firstName%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%firstName%', '%user.firstName%');

UPDATE `default_email_template` SET body = REPLACE(body, '%lastName%', '%user.lastName%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%lastName%', '%user.lastName%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%lastName%', '%user.lastName%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%lastName%', '%user.lastName%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%lastName%', '%user.lastName%');

UPDATE `default_email_template` SET body = REPLACE(body, '%domainName%', '%domain.name%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%domainName%', '%domain.name%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%domainName%', '%domain.name%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%domainName%', '%domain.name%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%domainName%', '%domain.name%');

UPDATE `default_email_template` SET body = REPLACE(body, '%domainUrl%', '%domain.url%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%domainUrl%', '%domain.url%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%domainUrl%', '%domain.url%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%domainUrl%', '%domain.url%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%domainUrl%', '%domain.url%');

UPDATE `default_email_template` SET body = REPLACE(body, '%domainSupportEmail%', '%domain.supportEmail%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%domainSupportEmail%', '%domain.supportEmail%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%domainSupportEmail%', '%domain.supportEmail%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%domainSupportEmail%', '%domain.supportEmail%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%domainSupportEmail%', '%domain.supportEmail%');

UPDATE `default_email_template` SET body = REPLACE(body, '%transactionType%', '%cashier.transactionType%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%transactionType%', '%cashier.transactionType%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%transactionType%', '%cashier.transactionType%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%transactionType%', '%cashier.transactionType%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%transactionType%', '%cashier.transactionType%');

UPDATE `default_email_template` SET body = REPLACE(body, '%transactionId%', '%cashier.transactionId%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%transactionId%', '%cashier.transactionId%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%transactionId%', '%cashier.transactionId%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%transactionId%', '%cashier.transactionId%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%transactionId%', '%cashier.transactionId%');

UPDATE `default_email_template` SET body = REPLACE(body, '%amount%', '%cashier.amount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%amount%', '%cashier.amount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%amount%', '%cashier.amount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%amount%', '%cashier.amount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%amount%', '%cashier.amount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%processorMethod%', '%cashier.processorMethod%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%processorMethod%', '%cashier.processorMethod%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%processorMethod%', '%cashier.processorMethod%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%processorMethod%', '%cashier.processorMethod%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%processorMethod%', '%cashier.processorMethod%');

UPDATE `default_email_template` SET body = REPLACE(body, '%processorReference%', '%cashier.processorReference%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%processorReference%', '%cashier.processorReference%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%processorReference%', '%cashier.processorReference%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%processorReference%', '%cashier.processorReference%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%processorReference%', '%cashier.processorReference%');

UPDATE `default_email_template` SET body = REPLACE(body, '%processorResponse%', '%cashier.processorResponse%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%processorResponse%', '%cashier.processorResponse%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%processorResponse%', '%cashier.processorResponse%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%processorResponse%', '%cashier.processorResponse%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%processorResponse%', '%cashier.processorResponse%');

UPDATE `default_email_template` SET body = REPLACE(body, '%request%', '%cashier.request%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%request%', '%cashier.request%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%request%', '%cashier.request%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%request%', '%cashier.request%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%request%', '%cashier.request%');

UPDATE `default_email_template` SET body = REPLACE(body, '%response%', '%cashier.response%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%response%', '%cashier.response%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%response%', '%cashier.response%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%response%', '%cashier.response%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%response%', '%cashier.response%');

UPDATE `default_email_template` SET body = REPLACE(body, '%billingDescriptor%', '%cashier.billingDescriptor%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%billingDescriptor%', '%cashier.billingDescriptor%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%billingDescriptor%', '%cashier.billingDescriptor%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%billingDescriptor%', '%cashier.billingDescriptor%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%billingDescriptor%', '%cashier.billingDescriptor%');

UPDATE `default_email_template` SET body = REPLACE(body, '%transactionFee%', '%cashier.transactionFee%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%transactionFee%', '%cashier.transactionFee%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%transactionFee%', '%cashier.transactionFee%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%transactionFee%', '%cashier.transactionFee%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%transactionFee%', '%cashier.transactionFee%');

UPDATE `default_email_template` SET body = REPLACE(body, '%brand%', '%domain.name%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%brand%', '%domain.name%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%brand%', '%domain.name%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%brand%', '%domain.name%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%brand%', '%domain.name%');

UPDATE `default_email_template` SET body = REPLACE(body, '%accountStatus%', '%user.accountStatus%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%accountStatus%', '%user.accountStatus%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%accountStatus%', '%user.accountStatus%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%accountStatus%', '%user.accountStatus%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%accountStatus%', '%user.accountStatus%');

UPDATE `default_email_template` SET body = REPLACE(body, '%kycStatus%', '%user.verificationStatus%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%kycStatus%', '%user.verificationStatus%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%kycStatus%', '%user.verificationStatus%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%kycStatus%', '%user.verificationStatus%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%kycStatus%', '%user.verificationStatus%');

UPDATE `default_email_template` SET body = REPLACE(body, '%ageVerified%', '%user.ageVerified%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%ageVerified%', '%user.ageVerified%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%ageVerified%', '%user.ageVerified%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%ageVerified%', '%user.ageVerified%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%ageVerified%', '%user.ageVerified%');

UPDATE `default_email_template` SET body = REPLACE(body, '%addressVerified%', '%user.addressVerified%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%addressVerified%', '%user.addressVerified%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%addressVerified%', '%user.addressVerified%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%addressVerified%', '%user.addressVerified%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%addressVerified%', '%user.addressVerified%');

UPDATE `default_email_template` SET body = REPLACE(body, '%fileName1%', '%document.fileName1%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%fileName1%', '%document.fileName1%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%fileName1%', '%document.fileName1%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%fileName1%', '%document.fileName1%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%fileName1%', '%document.fileName1%');

UPDATE `default_email_template` SET body = REPLACE(body, '%fileLink1%', '%document.fileLink1%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%fileLink1%', '%document.fileLink1%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%fileLink1%', '%document.fileLink1%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%fileLink1%', '%document.fileLink1%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%fileLink1%', '%document.fileLink1%');

UPDATE `default_email_template` SET body = REPLACE(body, '%fileTimestamp1%', '%document.fileTimestamp1%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%fileTimestamp1%', '%document.fileTimestamp1%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%fileTimestamp1%', '%document.fileTimestamp1%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%fileTimestamp1%', '%document.fileTimestamp1%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%fileTimestamp1%', '%document.fileTimestamp1%');

UPDATE `default_email_template` SET body = REPLACE(body, '%fileName2%', '%document.fileName2%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%fileName2%', '%document.fileName2%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%fileName2%', '%document.fileName2%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%fileName2%', '%document.fileName2%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%fileName2%', '%document.fileName2%');

UPDATE `default_email_template` SET body = REPLACE(body, '%fileLink2%', '%document.fileLink2%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%fileLink2%', '%document.fileLink2%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%fileLink2%', '%document.fileLink2%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%fileLink2%', '%document.fileLink2%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%fileLink2%', '%document.fileLink2%');

UPDATE `default_email_template` SET body = REPLACE(body, '%fileTimestamp2%', '%document.fileTimestamp2%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%fileTimestamp2%', '%document.fileTimestamp2%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%fileTimestamp2%', '%document.fileTimestamp2%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%fileTimestamp2%', '%document.fileTimestamp2%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%fileTimestamp2%', '%document.fileTimestamp2%');

UPDATE `default_email_template` SET body = REPLACE(body, '%documentType%', '%document.type%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%documentType%', '%document.type%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%documentType%', '%document.type%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%documentType%', '%document.type%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%documentType%', '%document.type%');

UPDATE `email_template_revision` etr JOIN email_template et on etr.email_template_id = et.id
SET etr.body = REPLACE(etr.body, 'userGuid', '%user.guid%')
WHERE et.name LIKE 'verification.document';
UPDATE `email_template_revision` etr JOIN email_template et on etr.email_template_id = et.id
SET etr.subject = REPLACE(etr.subject, 'userGuid', '%user.guid%')
WHERE et.name LIKE 'verification.document';

UPDATE `email_template_revision` etr JOIN email_template et on etr.email_template_id = et.id
SET etr.body = REPLACE(etr.body, 'documentGuid', '%document.id%')
WHERE et.name LIKE 'verification.document';
UPDATE `email_template_revision` etr JOIN email_template et on etr.email_template_id = et.id
SET etr.subject = REPLACE(etr.subject, 'documentGuid', '%document.id%')
WHERE et.name LIKE 'verification.document';

UPDATE `default_email_template` SET body = REPLACE(body, '%notificationMethod%', '%cashier.notificationMethod%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%notificationMethod%', '%cashier.notificationMethod%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%notificationMethod%', '%cashier.notificationMethod%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%notificationMethod%', '%cashier.notificationMethod%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%notificationMethod%', '%cashier.notificationMethod%');

UPDATE `default_email_template` SET body = REPLACE(body, '%createdDate%', '%user.createdDate%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%createdDate%', '%user.createdDate%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%createdDate%', '%user.createdDate%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%createdDate%', '%user.createdDate%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%createdDate%', '%user.createdDate%');

UPDATE `default_email_template` SET body = REPLACE(body, '%dateOfBirth%', '%user.dateOfBirth%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%dateOfBirth%', '%user.dateOfBirth%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%dateOfBirth%', '%user.dateOfBirth%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%dateOfBirth%', '%user.dateOfBirth%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%dateOfBirth%', '%user.dateOfBirth%');

UPDATE `default_email_template` SET body = REPLACE(body, '%playThroughCents%', '%casino.playThroughCents%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%playThroughCents%', '%casino.playThroughCents%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%playThroughCents%', '%casino.playThroughCents%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%playThroughCents%', '%casino.playThroughCents%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%playThroughCents%', '%casino.playThroughCents%');

UPDATE `default_email_template` SET body = REPLACE(body, '%playThroughRequiredCents%', '%casino.playThroughRequiredCents%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%playThroughRequiredCents%', '%casino.playThroughRequiredCents%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%playThroughRequiredCents%', '%casino.playThroughRequiredCents%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%playThroughRequiredCents%', '%casino.playThroughRequiredCents%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%playThroughRequiredCents%', '%casino.playThroughRequiredCents%');

UPDATE `default_email_template` SET body = REPLACE(body, '%triggerAmount%', '%casino.triggerAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%triggerAmount%', '%casino.triggerAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%triggerAmount%', '%casino.triggerAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%triggerAmount%', '%casino.triggerAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%triggerAmount%', '%casino.triggerAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%bonusAmount%', '%casino.bonusAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%bonusAmount%', '%casino.bonusAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%bonusAmount%', '%casino.bonusAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%bonusAmount%', '%casino.bonusAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%bonusAmount%', '%casino.bonusAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%bonusPercentage%', '%casino.bonusPercentage%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%bonusPercentage%', '%casino.bonusPercentage%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%bonusPercentage%', '%casino.bonusPercentage%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%bonusPercentage%', '%casino.bonusPercentage%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%bonusPercentage%', '%casino.bonusPercentage%');

UPDATE `default_email_template` SET body = REPLACE(body, '%bonusCode%', '%casino.bonusCode%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%bonusCode%', '%casino.bonusCode%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%bonusCode%', '%casino.bonusCode%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%bonusCode%', '%casino.bonusCode%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%bonusCode%', '%casino.bonusCode%');

UPDATE `default_email_template` SET body = REPLACE(body, '%bonusName%', '%casino.bonusName%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%bonusName%', '%casino.bonusName%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%bonusName%', '%casino.bonusName%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%bonusName%', '%casino.bonusName%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%bonusName%', '%casino.bonusName%');

UPDATE `default_email_template` SET body = REPLACE(body, '%name%', '%report.name%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%name%', '%report.name%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%name%', '%report.name%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%name%', '%report.name%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%name%', '%report.name%');

UPDATE `default_email_template` SET body = REPLACE(body, '%startedOn%', '%report.startedOn%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%startedOn%', '%report.startedOn%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%startedOn%', '%report.startedOn%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%startedOn%', '%report.startedOn%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%startedOn%', '%report.startedOn%');

UPDATE `default_email_template` SET body = REPLACE(body, '%completedOn%', '%report.completedOn%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%completedOn%', '%report.completedOn%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%completedOn%', '%report.completedOn%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%completedOn%', '%report.completedOn%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%completedOn%', '%report.completedOn%');

UPDATE `default_email_template` SET body = REPLACE(body, '%startedBy%', '%report.startedBy%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%startedBy%', '%report.startedBy%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%startedBy%', '%report.startedBy%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%startedBy%', '%report.startedBy%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%startedBy%', '%report.startedBy%');

UPDATE `default_email_template` SET body = REPLACE(body, '%totalRecords%', '%report.totalRecords%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%totalRecords%', '%report.totalRecords%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%totalRecords%', '%report.totalRecords%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%totalRecords%', '%report.totalRecords%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%totalRecords%', '%report.totalRecords%');

UPDATE `default_email_template` SET body = REPLACE(body, '%processedRecords%', '%report.processedRecords%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%processedRecords%', '%report.processedRecords%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%processedRecords%', '%report.processedRecords%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%processedRecords%', '%report.processedRecords%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%processedRecords%', '%report.processedRecords%');

UPDATE `default_email_template` SET body = REPLACE(body, '%currentBalance%', '%report.currentBalance%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%currentBalance%', '%report.currentBalance%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%currentBalance%', '%report.currentBalance%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%currentBalance%', '%report.currentBalance%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%currentBalance%', '%report.currentBalance%');

UPDATE `default_email_template` SET body = REPLACE(body, '%currentBalanceCasinoBonus%', '%report.currentBalanceCasinoBonus%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%currentBalanceCasinoBonus%', '%report.currentBalanceCasinoBonus%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%currentBalanceCasinoBonus%', '%report.currentBalanceCasinoBonus%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%currentBalanceCasinoBonus%', '%report.currentBalanceCasinoBonus%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%currentBalanceCasinoBonus%', '%report.currentBalanceCasinoBonus%');

UPDATE `default_email_template` SET body = REPLACE(body, '%currentBalanceCasinoBonusPending%', '%report.currentBalanceCasinoBonusPending%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%currentBalanceCasinoBonusPending%', '%report.currentBalanceCasinoBonusPending%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%currentBalanceCasinoBonusPending%', '%report.currentBalanceCasinoBonusPending%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%currentBalanceCasinoBonusPending%', '%report.currentBalanceCasinoBonusPending%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%currentBalanceCasinoBonusPending%', '%report.currentBalanceCasinoBonusPending%');

UPDATE `default_email_template` SET body = REPLACE(body, '%periodOpeningBalance%', '%report.periodOpeningBalance%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%periodOpeningBalance%', '%report.periodOpeningBalance%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%periodOpeningBalance%', '%report.periodOpeningBalance%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%periodOpeningBalance%', '%report.periodOpeningBalance%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%periodOpeningBalance%', '%report.periodOpeningBalance%');

UPDATE `default_email_template` SET body = REPLACE(body, '%periodClosingBalance%', '%report.periodClosingBalance%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%periodClosingBalance%', '%report.periodClosingBalance%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%periodClosingBalance%', '%report.periodClosingBalance%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%periodClosingBalance%', '%report.periodClosingBalance%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%periodClosingBalance%', '%report.periodClosingBalance%');

UPDATE `default_email_template` SET body = REPLACE(body, '%periodOpeningBalanceCasinoBonus%', '%report.periodOpeningBalanceCasinoBonus%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%periodOpeningBalanceCasinoBonus%', '%report.periodOpeningBalanceCasinoBonus%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%periodOpeningBalanceCasinoBonus%', '%report.periodOpeningBalanceCasinoBonus%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%periodOpeningBalanceCasinoBonus%', '%report.periodOpeningBalanceCasinoBonus%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%periodOpeningBalanceCasinoBonus%', '%report.periodOpeningBalanceCasinoBonus%');

UPDATE `default_email_template` SET body = REPLACE(body, '%periodClosingBalanceCasinoBonus%', '%report.periodClosingBalanceCasinoBonus%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%periodClosingBalanceCasinoBonus%', '%report.periodClosingBalanceCasinoBonus%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%periodClosingBalanceCasinoBonus%', '%report.periodClosingBalanceCasinoBonus%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%periodClosingBalanceCasinoBonus%', '%report.periodClosingBalanceCasinoBonus%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%periodClosingBalanceCasinoBonus%', '%report.periodClosingBalanceCasinoBonus%');

UPDATE `default_email_template` SET body = REPLACE(body, '%periodOpeningBalanceCasinoBonusPending%', '%report.periodOpeningBalanceCasinoBonusPending%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%periodOpeningBalanceCasinoBonusPending%', '%report.periodOpeningBalanceCasinoBonusPending%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%periodOpeningBalanceCasinoBonusPending%', '%report.periodOpeningBalanceCasinoBonusPending%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%periodOpeningBalanceCasinoBonusPending%', '%report.periodOpeningBalanceCasinoBonusPending%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%periodOpeningBalanceCasinoBonusPending%', '%report.periodOpeningBalanceCasinoBonusPending%');

UPDATE `default_email_template` SET body = REPLACE(body, '%periodClosingBalanceCasinoBonusPending%', '%report.periodClosingBalanceCasinoBonusPending%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%periodClosingBalanceCasinoBonusPending%', '%report.periodClosingBalanceCasinoBonusPending%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%periodClosingBalanceCasinoBonusPending%', '%report.periodClosingBalanceCasinoBonusPending%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%periodClosingBalanceCasinoBonusPending%', '%report.periodClosingBalanceCasinoBonusPending%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%periodClosingBalanceCasinoBonusPending%', '%report.periodClosingBalanceCasinoBonusPending%');

UPDATE `default_email_template` SET body = REPLACE(body, '%depositAmount%', '%report.depositAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%depositAmount%', '%report.depositAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%depositAmount%', '%report.depositAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%depositAmount%', '%report.depositAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%depositAmount%', '%report.depositAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%depositCount%', '%report.depositCount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%depositCount%', '%report.depositCount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%depositCount%', '%report.depositCount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%depositCount%', '%report.depositCount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%depositCount%', '%report.depositCount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%payoutAmount%', '%report.payoutAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%payoutAmount%', '%report.payoutAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%payoutAmount%', '%report.payoutAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%payoutAmount%', '%report.payoutAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%payoutAmount%', '%report.payoutAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%payoutCount%', '%report.payoutCount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%payoutCount%', '%report.payoutCount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%payoutCount%', '%report.payoutCount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%payoutCount%', '%report.payoutCount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%payoutCount%', '%report.payoutCount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%balanceAdjustAmount%', '%report.balanceAdjustAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%balanceAdjustAmount%', '%report.balanceAdjustAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%balanceAdjustAmount%', '%report.balanceAdjustAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%balanceAdjustAmount%', '%report.balanceAdjustAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%balanceAdjustAmount%', '%report.balanceAdjustAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%balanceAdjustCount%', '%report.balanceAdjustCount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%balanceAdjustCount%', '%report.balanceAdjustCount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%balanceAdjustCount%', '%report.balanceAdjustCount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%balanceAdjustCount%', '%report.balanceAdjustCount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%balanceAdjustCount%', '%report.balanceAdjustCount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBetAmount%', '%report.casinoBetAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBetAmount%', '%report.casinoBetAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBetAmount%', '%report.casinoBetAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBetAmount%', '%report.casinoBetAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBetAmount%', '%report.casinoBetAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBetCount%', '%report.casinoBetCount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBetCount%', '%report.casinoBetCount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBetCount%', '%report.casinoBetCount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBetCount%', '%report.casinoBetCount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBetCount%', '%report.casinoBetCount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoWinAmount%', '%report.casinoWinAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoWinAmount%', '%report.casinoWinAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoWinAmount%', '%report.casinoWinAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoWinAmount%', '%report.casinoWinAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoWinAmount%', '%report.casinoWinAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoWinCount%', '%report.casinoWinCount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoWinCount%', '%report.casinoWinCount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoWinCount%', '%report.casinoWinCount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoWinCount%', '%report.casinoWinCount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoWinCount%', '%report.casinoWinCount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoNetAmount%', '%report.casinoNetAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoNetAmount%', '%report.casinoNetAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoNetAmount%', '%report.casinoNetAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoNetAmount%', '%report.casinoNetAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoNetAmount%', '%report.casinoNetAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusBetAmount%', '%report.casinoBonusBetAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusBetAmount%', '%report.casinoBonusBetAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusBetAmount%', '%report.casinoBonusBetAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusBetAmount%', '%report.casinoBonusBetAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusBetAmount%', '%report.casinoBonusBetAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusBetCount%', '%report.casinoBonusBetCount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusBetCount%', '%report.casinoBonusBetCount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusBetCount%', '%report.casinoBonusBetCount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusBetCount%', '%report.casinoBonusBetCount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusBetCount%', '%report.casinoBonusBetCount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusWinAmount%', '%report.casinoBonusWinAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusWinAmount%', '%report.casinoBonusWinAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusWinAmount%', '%report.casinoBonusWinAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusWinAmount%', '%report.casinoBonusWinAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusWinAmount%', '%report.casinoBonusWinAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusWinCount%', '%report.casinoBonusWinCount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusWinCount%', '%report.casinoBonusWinCount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusWinCount%', '%report.casinoBonusWinCount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusWinCount%', '%report.casinoBonusWinCount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusWinCount%', '%report.casinoBonusWinCount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusNetAmount%', '%report.casinoBonusNetAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusNetAmount%', '%report.casinoBonusNetAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusNetAmount%', '%report.casinoBonusNetAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusNetAmount%', '%report.casinoBonusNetAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusNetAmount%', '%report.casinoBonusNetAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusPendingAmount%', '%report.casinoBonusPendingAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusPendingAmount%', '%report.casinoBonusPendingAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusPendingAmount%', '%report.casinoBonusPendingAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusPendingAmount%', '%report.casinoBonusPendingAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusPendingAmount%', '%report.casinoBonusPendingAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusTransferToBonusPendingAmount%', '%report.casinoBonusTransferToBonusPendingAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusTransferToBonusPendingAmount%', '%report.casinoBonusTransferToBonusPendingAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusTransferToBonusPendingAmount%', '%report.casinoBonusTransferToBonusPendingAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusTransferToBonusPendingAmount%', '%report.casinoBonusTransferToBonusPendingAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusTransferToBonusPendingAmount%', '%report.casinoBonusTransferToBonusPendingAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusTransferFromBonusPendingAmount%', '%report.casinoBonusTransferFromBonusPendingAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusTransferFromBonusPendingAmount%', '%report.casinoBonusTransferFromBonusPendingAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusTransferFromBonusPendingAmount%', '%report.casinoBonusTransferFromBonusPendingAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusTransferFromBonusPendingAmount%', '%report.casinoBonusTransferFromBonusPendingAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusTransferFromBonusPendingAmount%', '%report.casinoBonusTransferFromBonusPendingAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusPendingCancelAmount%', '%report.casinoBonusPendingCancelAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusPendingCancelAmount%', '%report.casinoBonusPendingCancelAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusPendingCancelAmount%', '%report.casinoBonusPendingCancelAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusPendingCancelAmount%', '%report.casinoBonusPendingCancelAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusPendingCancelAmount%', '%report.casinoBonusPendingCancelAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusPendingCount%', '%report.casinoBonusPendingCount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusPendingCount%', '%report.casinoBonusPendingCount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusPendingCount%', '%report.casinoBonusPendingCount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusPendingCount%', '%report.casinoBonusPendingCount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusPendingCount%', '%report.casinoBonusPendingCount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusActivateAmount%', '%report.casinoBonusActivateAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusActivateAmount%', '%report.casinoBonusActivateAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusActivateAmount%', '%report.casinoBonusActivateAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusActivateAmount%', '%report.casinoBonusActivateAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusActivateAmount%', '%report.casinoBonusActivateAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusTransferToBonusAmount%', '%report.casinoBonusTransferToBonusAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusTransferToBonusAmount%', '%report.casinoBonusTransferToBonusAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusTransferToBonusAmount%', '%report.casinoBonusTransferToBonusAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusTransferToBonusAmount%', '%report.casinoBonusTransferToBonusAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusTransferToBonusAmount%', '%report.casinoBonusTransferToBonusAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusTransferFromBonusAmount%', '%report.casinoBonusTransferFromBonusAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusTransferFromBonusAmount%', '%report.casinoBonusTransferFromBonusAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusTransferFromBonusAmount%', '%report.casinoBonusTransferFromBonusAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusTransferFromBonusAmount%', '%report.casinoBonusTransferFromBonusAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusTransferFromBonusAmount%', '%report.casinoBonusTransferFromBonusAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusCancelAmount%', '%report.casinoBonusCancelAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusCancelAmount%', '%report.casinoBonusCancelAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusCancelAmount%', '%report.casinoBonusCancelAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusCancelAmount%', '%report.casinoBonusCancelAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusCancelAmount%', '%report.casinoBonusCancelAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusExpireAmount%', '%report.casinoBonusExpireAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusExpireAmount%', '%report.casinoBonusExpireAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusExpireAmount%', '%report.casinoBonusExpireAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusExpireAmount%', '%report.casinoBonusExpireAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusExpireAmount%', '%report.casinoBonusExpireAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%casinoBonusMaxPayoutExcessAmount%', '%report.casinoBonusMaxPayoutExcessAmount%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%casinoBonusMaxPayoutExcessAmount%', '%report.casinoBonusMaxPayoutExcessAmount%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%casinoBonusMaxPayoutExcessAmount%', '%report.casinoBonusMaxPayoutExcessAmount%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%casinoBonusMaxPayoutExcessAmount%', '%report.casinoBonusMaxPayoutExcessAmount%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%casinoBonusMaxPayoutExcessAmount%', '%report.casinoBonusMaxPayoutExcessAmount%');

UPDATE `default_email_template` SET body = REPLACE(body, '%OptOutEmailUrl%', '%user.optOutEmailUrl%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%OptOutEmailUrl%', '%user.optOutEmailUrl%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%OptOutEmailUrl%', '%user.optOutEmailUrl%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%OptOutEmailUrl%', '%user.optOutEmailUrl%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%OptOutEmailUrl%', '%user.optOutEmailUrl%');

UPDATE `default_email_template` SET body = REPLACE(body, '%to%', '%settlement.to%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%to%', '%settlement.to%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%to%', '%settlement.to%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%to%', '%settlement.to%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%to%', '%settlement.to%');

UPDATE `default_email_template` SET body = REPLACE(body, '%period%', '%settlement.period%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%period%', '%settlement.period%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%to%', '%settlement.period%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%period%', '%settlement.period%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%period%', '%settlement.period%');

UPDATE `default_email_template` SET body = REPLACE(body, '%playingToLevel%', '%xp.playingToLevel%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%playingToLevel%', '%xp.playingToLevel%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%playingToLevel%', '%xp.playingToLevel%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%playingToLevel%', '%xp.playingToLevel%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%playingToLevel%', '%xp.playingToLevel%');

UPDATE `default_email_template` SET body = REPLACE(body, '%progress%', '%xp.progress%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%progress%', '%xp.progress%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%progress%', '%xp.progress%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%progress%', '%xp.progress%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%progress%', '%xp.progress%');

UPDATE `default_email_template` SET body = REPLACE(body, '%bonusCode%', '%xp.bonusCode%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%bonusCode%', '%xp.bonusCode%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%bonusCode%', '%xp.bonusCode%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%bonusCode%', '%xp.bonusCode%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%bonusCode%', '%xp.bonusCode%');

UPDATE `default_email_template` SET body = REPLACE(body, '%isMilestone%', '%xp.isMilestone%');
UPDATE `default_email_template` SET subject = REPLACE(subject, '%isMilestone%', '%xp.isMilestone%');
UPDATE `default_email_template_placeholder` SET name = REPLACE(name, '%isMilestone%', '%xp.isMilestone%');
UPDATE `email_template_revision` SET body = REPLACE(body, '%isMilestone%', '%xp.isMilestone%');
UPDATE `email_template_revision` SET subject = REPLACE(subject, '%isMilestone%', '%xp.isMilestone%');

