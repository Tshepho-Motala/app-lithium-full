--
-- Dumping data for table `domain`
--
INSERT INTO `domain` VALUES (1,'\0','The root of all other domains, for internal use to manage all customer domains','Root Domain','','default','\0',NULL,NULL,'http://www.playsafesa.com',0,NULL);
--
-- Dumping data for table `provider_type`
--
INSERT INTO `provider_type` VALUES (1,'USER',0),(2,'AUTH',0),(3,'CASINO',0),(4,'CASHIER',0);
--
-- Dumping data for table `provider`
--
INSERT INTO `provider` VALUES (1,'','user-provider-internal',1,'service-user-provider-internal',0,1,1),(2,'','auth-provider-internal',1,'service-auth-provider-internal',0,1,2);
