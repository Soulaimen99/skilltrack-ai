<#-- Custom Keycloak Registration Page -->
<#import "user-profile-commons.ftl" as userProfileCommons>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${msg("registerTitle")}</title>
    <link rel="stylesheet" href="${url.resourcesPath}/css/style.css">
</head>
<body>
<div class="kc-container">
    <h1>${msg("registerTitle")}</h1>

    <#-- Display error message if present -->
    <#if message?has_content && message.type == "error">
        <div class="alert-error">${message.summary}</div>
    </#if>

    <form id="kc-register-form" action="${url.registrationAction}" method="post">

        <#-- Render all fields from declarative user profile (username, email, etc.) -->
        <@userProfileCommons.userProfileFormFields />

        <#-- Password -->
        <div class="form-group">
            <label for="password">${msg("password")}</label>
            <input type="password" id="password" name="password"
                   autocomplete="new-password" required
                   aria-invalid="<#if messagesPerField.existsError('password')>true</#if>"/>
            <#if messagesPerField.existsError('password')>
                <span class="kc-feedback-text">${kcSanitize(messagesPerField.get('password'))?no_esc}</span>
            </#if>
        </div>

        <#-- Confirm Password -->
        <div class="form-group">
            <label for="password-confirm">${msg("passwordConfirm")}</label>
            <input type="password" id="password-confirm" name="password-confirm"
                   autocomplete="new-password" required
                   aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"/>
            <#if messagesPerField.existsError('password-confirm')>
                <span class="kc-feedback-text">${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}</span>
            </#if>
        </div>

        <input type="submit" id="register" name="register" value="${msg("doRegister")}"/>
        <a href="${url.loginUrl}">${msg("doCancel")}</a>
    </form>
</div>
</body>
</html>
