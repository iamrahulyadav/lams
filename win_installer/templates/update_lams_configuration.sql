update lams_configuration set config_value='@URL@' where config_key='ServerURL';
update lams_configuration set config_value='@INSTDIR@' where config_key='LamsHome';
update lams_configuration set config_value='@TEMPDIR@' where config_key='TempDir';
update lams_configuration set config_value='@DUMPDIR@' where config_key='DumpDir';
update lams_configuration set config_value='@EARDIR@' where config_key='EARDir';
update lams_configuration set config_value='@REPOSITORYDIR@' where config_key='ContentRepositoryPath';
update lams_configuration set config_value='@LOCALE@' where config_key='ServerLanguage';
update lams_configuration set config_value='@LOCALE_DIRECTION@' where config_key='ServerPageDirection';
update lams_configuration set config_value='@WILDFIRE_DOMAIN@' where config_key='XmppDomain';
update lams_configuration set config_value='@WILDFIRE_CONFERENCE@' where config_key='XmppConference';
update lams_configuration set config_value='@WILDFIRE_USER@' where config_key='XmppAdmin';
update lams_configuration set config_value='@WILDFIRE_PASS@' where config_key='XmppPassword';
update lams_user set login='@LAMS_USER@', password=sha1('@LAMS_PASS@') where user_id=1;
update lams_user set locale_id=(select locale_id from lams_supported_locale where language_iso_code=(SELECT SUBSTRING_INDEX('@LOCALE@', '_', 1)) and country_iso_code=(SELECT SUBSTRING_INDEX('@LOCALE@', '_', -1))) where user_id=1;
update lams_user set login='@LAMS_USER@', password=sha1('@LAMS_PASS@') where login='test';
update lams_user set login='@LAMS_USER@', password=sha1('@LAMS_PASS@') where login='test1';
update lams_user set login='@LAMS_USER@', password=sha1('@LAMS_PASS@') where login='test2';
update lams_user set login='@LAMS_USER@', password=sha1('@LAMS_PASS@') where login='test3';
update lams_user set login='@LAMS_USER@', password=sha1('@LAMS_PASS@') where login='test4';