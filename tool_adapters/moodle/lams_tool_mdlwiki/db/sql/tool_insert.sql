-- CVS ID: $Id$
 
INSERT INTO lams_tool
(
tool_signature,
service_name,
tool_display_name,
description,
tool_identifier,
tool_version,
learning_library_id,
default_tool_content_id,
valid_flag,
grouping_support_type_id,
supports_run_offline_flag,
learner_url,
learner_preview_url,
learner_progress_url,
author_url,
monitor_url,
define_later_url,
export_pfolio_learner_url,
export_pfolio_class_url,
contribute_url,
moderation_url,
help_url,
admin_url,
language_file,
create_date_time,
modified_date_time,
ext_lms_id,
supports_outputs
)
VALUES
(
'mdwiki10',
'mdlWikiService',
'MdlWiki',
'MdlWiki',
'mdlWiki',
'@tool_version@',
NULL,
NULL,
0,
2,
1,
'tool/mdwiki10/learning.do?mode=learner',
'tool/mdwiki10/learning.do?mode=author',
'tool/mdwiki10/learning.do?mode=teacher',
'tool/mdwiki10/authoring.do',
'tool/mdwiki10/monitoring.do',
'tool/mdwiki10/authoring.do?mode=teacher',
'tool/mdwiki10/exportPortfolio?mode=learner',
'tool/mdwiki10/exportPortfolio?mode=teacher',
'tool/mdwiki10/contribute.do',
'tool/mdwiki10/moderate.do',
'http://wiki.lamsfoundation.org/display/lamsdocs/mdwiki10',
'tool/mdwiki10/mdwiki10admin.do',
'org.lamsfoundation.lams.tool.mdwiki.ApplicationResources',
NOW(),
NOW(),
'moodle',
true
)
