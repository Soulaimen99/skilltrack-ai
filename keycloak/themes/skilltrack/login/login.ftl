<#-- Skip template.ftl and keep it raw and clean -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="${url.resourcesPath}/css/style.css">
    <link rel="icon" href="${url.resourcesPath}/favicon.ico" type="image/x-icon">
</head>
<body>

<div id="kc-container-wrapper">
    <div id="kc-container">

        <div id="kc-header-wrapper">
            <h1 id="kc-page-title">Login</h1>
        </div>

        <#-- Show error if present -->
        <#if message?has_content && message.type == "error">
            <div class="alert-error">${message.summary}</div>
        </#if>

        <form id="kc-form-login" action="${url.loginAction}" method="post">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" value="${login.username!}" required autofocus
                       autocomplete="username">
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required autocomplete="current-password">
            </div>

            <#if realm.rememberMe??>
                <div class="form-group">
                    <label>
                        <input type="checkbox" name="rememberMe"
                               <#if login.rememberMe??>checked</#if>> Remember me
                    </label>
                </div>
            </#if>

            <div class="form-group">
                <input type="submit" value="Login">
            </div>
        </form>

        <#if realm.resetPasswordAllowed || realm.registrationAllowed>
            <div id="kc-info">
                <#if realm.resetPasswordAllowed>
                    <a href="${url.loginResetCredentialsUrl}">Forgot Password?</a>
                </#if>
                <#if realm.registrationAllowed>
                    <br>
                    <a href="${url.registrationUrl}">Register</a>
                </#if>
            </div>
        </#if>

    </div>
</div>

</body>
</html>
