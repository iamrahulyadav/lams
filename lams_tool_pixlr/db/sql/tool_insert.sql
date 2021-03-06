INSERT INTO lams_tool
(
tool_signature,
service_name,
tool_display_name,
description,
tool_identifier,
tool_version,
valid_flag,
grouping_support_type_id,
learner_url,
learner_preview_url,
learner_progress_url,
author_url,
monitor_url,
help_url,
language_file,
create_date_time,
modified_date_time,
supports_outputs,
admin_url
)
VALUES
(
'lapixl10',
'pixlrService',
'Pixlr',
'Pixlr',
'pixlr',
'@tool_version@',
0,
2,
'tool/lapixl10/learning.do?mode=learner',
'tool/lapixl10/learning.do?mode=author',
'tool/lapixl10/learning.do?mode=teacher',
'tool/lapixl10/authoring.do',
'tool/lapixl10/monitoring.do',
'http://wiki.lamsfoundation.org/display/lamsdocs/lapixl10',
'org.lamsfoundation.lams.tool.pixlr.ApplicationResources',
NOW(),
NOW(),
0,
'tool/lapixl10/lapixl10admin.do'
)