insert into lams_configuration (config_key, config_value) values ('ServerURL','http://shaun.melcoe.mq.edu.au/lams/');
insert into lams_configuration (config_key, config_value) values ('ServerURLContextPath','lams/');
insert into lams_configuration (config_key, config_value) values ('Version','2.0.4 DEV');
insert into lams_configuration (config_key, config_value) values ('TempDir','/var/opt/lams/temp');
insert into lams_configuration (config_key, config_value) values ('DumpDir','/var/opt/lams/dump');
insert into lams_configuration (config_key, config_value) values ('EARDir','/usr/local/jboss-4.0.2/server/default/deploy/lams.ear');
insert into lams_configuration (config_key, config_value) values ('SMTPServer','');
insert into lams_configuration (config_key, config_value) values ('LamsSupportEmail','lams_support@melcoe.mq.edu.au');
insert into lams_configuration (config_key, config_value) values ('ContentRepositoryPath','/var/opt/lams/repository');
insert into lams_configuration (config_key, config_value) values ('UploadFileMaxSize','1048576');
insert into lams_configuration (config_key, config_value) values ('UploadLargeFileMaxSize','10485760');
insert into lams_configuration (config_key, config_value) values ('UploadFileMaxMemorySize','4096');
insert into lams_configuration (config_key, config_value) values ('ExecutableExtensions','.bat,.bin,.com,.cmd,.exe,.msi,.msp,.ocx,.pif,.scr,.sct,.sh,.shs,.vbs');
insert into lams_configuration (config_key, config_value) values ('UserInactiveTimeout','86400');
insert into lams_configuration (config_key, config_value) values ('UseCacheDebugListener','false');
insert into lams_configuration (config_key, config_value) values ('CleanupPreviewOlderThanDays','7');
insert into lams_configuration (config_key, config_value) values ('AuthoringActivitiesColour', 'true');
insert into lams_configuration (config_key, config_value) values ('AuthoringClientVersion','2.0.4.@datetimestamp@');
insert into lams_configuration (config_key, config_value) values ('MonitorClientVersion','2.0.4.@datetimestamp@');
insert into lams_configuration (config_key, config_value) values ('LearnerClientVersion','2.0.4.@datetimestamp@');
insert into lams_configuration (config_key, config_value) values ('ServerVersionNumber','2.0.4.@datetimestamp@');
insert into lams_configuration (config_key, config_value) values ('ServerLanguage','en_AU');
insert into lams_configuration (config_key, config_value) values ('ServerPageDirection','LTR');
insert into lams_configuration (config_key, config_value) values ('DictionaryDateCreated','2007-05-24');
insert into lams_configuration (config_key, config_value) values ('HelpURL','http://wiki.lamsfoundation.org/display/lamsdocs/');
insert into lams_configuration (config_key, config_value) values ('XmppDomain','shaun.melcoe.mq.edu.au');
insert into lams_configuration (config_key, config_value) values ('XmppConference','conference.shaun.melcoe.mq.edu.au');
insert into lams_configuration (config_key, config_value) values ('XmppAdmin','admin');
insert into lams_configuration (config_key, config_value) values ('XmppPassword','wildfire');
insert into lams_configuration (config_key, config_value) values ('DefaultFlashTheme','default');
insert into lams_configuration (config_key, config_value) values ('DefaultHTMLTheme','defaultHTML');
insert into lams_configuration (config_key, config_value) values ('AllowDirectLessonLaunch','false');
insert into lams_configuration (config_key, config_value) values ('LAMS_Community_enable','false');
insert into lams_configuration (config_key, config_value) values ('AllowLiveEdit','true');
insert into lams_configuration (config_key, config_value) values ('LDAPProvisioningEnabled','true');
insert into lams_configuration (config_key, config_value) values ('LDAPProviderURL','ldap://192.168.111.15');
insert into lams_configuration (config_key, config_value) values ('LDAPSecurityAuthentication','simple');
insert into lams_configuration (config_key, config_value) values ('LDAPPrincipalDNPrefix','cn=');
insert into lams_configuration (config_key, config_value) values ('LDAPPrincipalDNSuffix',',ou=Users,dc=melcoe,dc=mq,dc=edu,dc=au');
insert into lams_configuration (config_key, config_value) values ('LDAPSecurityProtocol','');
insert into lams_configuration (config_key, config_value) values ('LDAPTruststorePath','');
insert into lams_configuration (config_key, config_value) values ('LDAPTruststorePassword','');
insert into lams_configuration (config_key, config_value) values ('LDAPLoginAttr','uid');
insert into lams_configuration (config_key, config_value) values ('LDAPFNameAttr','givenName');
insert into lams_configuration (config_key, config_value) values ('LDAPLNameAttr','sn');
insert into lams_configuration (config_key, config_value) values ('LDAPEmailAttr','mail');
insert into lams_configuration (config_key, config_value) values ('LDAPAddr1Attr','postalAddress');
insert into lams_configuration (config_key, config_value) values ('LDAPAddr2Attr','');
insert into lams_configuration (config_key, config_value) values ('LDAPAddr3Attr','');
insert into lams_configuration (config_key, config_value) values ('LDAPCityAttr','l');
insert into lams_configuration (config_key, config_value) values ('LDAPStateAttr','st');
insert into lams_configuration (config_key, config_value) values ('LDAPPostcodeAttr','postalCode');
insert into lams_configuration (config_key, config_value) values ('LDAPCountryAttr','');
insert into lams_configuration (config_key, config_value) values ('LDAPDayPhoneAttr','telephoneNumber');
insert into lams_configuration (config_key, config_value) values ('LDAPEveningPhoneAttr','homePhone');
insert into lams_configuration (config_key, config_value) values ('LDAPFaxAttr','facsimileTelephoneNumber');
insert into lams_configuration (config_key, config_value) values ('LDAPMobileAttr','mobile');
insert into lams_configuration (config_key, config_value) values ('LDAPLocaleAttr','preferredLanguage');
insert into lams_configuration (config_key, config_value) values ('LDAPDisabledAttr','!accountStatus');
insert into lams_configuration (config_key, config_value) values ('LDAPOrgAttr','deetITSchoolCode');
insert into lams_configuration (config_key, config_value) values ('LDAPRolesAttr','memberOf');
insert into lams_configuration (config_key, config_value) values ('LDAPLearnerMap','Student;SchoolSupportStaff;Teacher;SeniorStaff;Principal');
insert into lams_configuration (config_key, config_value) values ('LDAPMonitorMap','SchoolSupportStaff;Teacher;SeniorStaff;Principal');
insert into lams_configuration (config_key, config_value) values ('LDAPAuthorMap','Teacher;SeniorStaff;Principal');
insert into lams_configuration (config_key, config_value) values ('LDAPGroupAdminMap','Teacher;SeniorStaff');
insert into lams_configuration (config_key, config_value) values ('LDAPGroupManagerMap','Principal');
insert into lams_configuration (config_key, config_value) values ('LDAPUpdateOnLogin', 'true');
insert into lams_configuration (config_key, config_value) values ('LDAPOrgField', 'code');
insert into lams_configuration (config_key, config_value) values ('LDAPOnlyOneOrg', 'true');
insert into lams_configuration (config_key, config_value) values ('LDAPEncryptPasswordFromBrowser', 'false');