--
-- Dumping data for table `category`
--
INSERT INTO `category` VALUES (1,'Super Network Admin Operations','AdminCategory');
--
-- Dumping data for table `status`
--
INSERT INTO `status` VALUES 
(1,'\0','User account is enabled','Enabled','',0),
(2,'\0','User account is disabled','Disabled','\0',0),
(3,'\0','Account disabled because user is underage.','Disabled_Underage','\0',0),
(4,'\0','Account disabled because of fraud confirmed.','Disabled_Fraud','\0',0),
(5,'\0','Account suspened because of fraud investigation.','Suspend_Fraud','\0',0);
--
-- Dumping data for table `transaction_type_account`
--
INSERT INTO `transaction_type_account` VALUES 
(1,'MANUAL_BALANCE_ADJUST','','',0),
(2,'MANUAL_BONUS_POKER','','',0),
(3,'MANUAL_BONUS_CASINO','','',0),
(4,'MANUAL_WITHDRAWAL_FEE','','',0),
(5,'AFFILIATE_PAYMENT','','',0);
--
-- Dumping data for table `domain`
--
INSERT INTO `domain` VALUES (1,'default');
--
-- Dumping data for table `group_table`
--
INSERT INTO `group_table` VALUES (1,'\0','Admin Group','','AdminGroup',1);
--
-- Dumping data for table `role`
--
INSERT INTO `role` VALUES (1,'Super Network Admin Role','Admin User','ADMIN',1);
--
-- Dumping data for table `grd`
--
INSERT INTO `grd` VALUES (1,'','',1,1,1);
--
-- Dumping data for table `user`
--
INSERT INTO `user` VALUES 
(1,NULL,NULL,NULL,NOW(),'\0','super@default.com','Super','Administrator','Gauteng',NULL,NULL,NULL,NOW(),'admin',1,NULL,NULL,1);
--
-- Dumping data for table `user_groups`
--
INSERT INTO `user_groups` VALUES (1,1);