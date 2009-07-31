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
'mdasgm10',
'mdlAssignmentService',
'MdlAssignment',
'MdlAssignment',
'mdlAssignment',
'@tool_version@',
NULL,
NULL,
0,
2,
1,
'tool/mdasgm10/learning.do?mode=learner',
'tool/mdasgm10/learning.do?mode=author',
'tool/mdasgm10/learning.do?mode=teacher',
'tool/mdasgm10/authoring.do',
'tool/mdasgm10/monitoring.do',
'tool/mdasgm10/authoring.do?mode=teacher',
'tool/mdasgm10/exportPortfolio?mode=learner',
'tool/mdasgm10/exportPortfolio?mode=teacher',
'tool/mdasgm10/contribute.do',
'tool/mdasgm10/moderate.do',
'http://wiki.lamsfoundation.org/display/lamsdocs/mdasgm10',
'tool/mdasgm10/mdasgm10admin.do',
'org.lamsfoundation.lams.tool.mdasgm.ApplicationResources',
NOW(),
NOW(),
'moodle',
true
)
