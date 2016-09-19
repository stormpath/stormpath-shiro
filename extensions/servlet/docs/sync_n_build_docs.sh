#! /bin/bash

# by default assume sibling directory as stormpath-shiro
STORMPATH_JAVA_SDK_DIR=../../../../stormpath-sdk-java

SDK_SERVLET_DOCS_DIR="${STORMPATH_JAVA_SDK_DIR}/docs"

FILES_TO_COPY=(
   _static
   _templates
   _themes
   login.rst
   social.rst
   logout.rst
   i18n.rst
)

for file_to_copy in ${FILES_TO_COPY[@]}; do

    file_path="${SDK_SERVLET_DOCS_DIR}/source/${file_to_copy}"

    if [ -f "${file_path}" ]; then
          sed 's_\.\./\.\..*/i18n\.properties_code/i18n.properties_g' "${file_path}" > "source/${file_to_copy}"
     else
          cp -R "${file_path}" source
    fi
done

mkdir -p code

CODE_FILES_TO_COPY=(
   extensions/servlet/src/main/resources/com/stormpath/sdk/servlet/i18n.properties
)

for file_to_copy in ${CODE_FILES_TO_COPY[@]}; do
    cp -R "${STORMPATH_JAVA_SDK_DIR}/${file_to_copy}" source/code
done

make html