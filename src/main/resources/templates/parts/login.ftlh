<#macro login path isRegisterForm>

<form action="${path}" method="post">
  <div class="form-group row">
    <label class="col-sm-2 col-form-label"> User Name :</label>
    <div class="col-sm-6">
        <input type="text" value="<#if user??>${user.username}</#if>"
               class="form-control ${(usernameError??)?string('is-invalid', '')}"
               name="username"
               placeholder="Имя пользователя"/>
        <#if usernameError??>
            <div class="invalid-feedback">
                ${usernameError}
            </div>
        </#if>
    </div>
  </div>
  <div class="form-group row">
    <label class="col-sm-2 col-form-label"> Password: </label>
    <div class="col-sm-6">
        <input type="password" name="password"
               class="form-control ${(passwordError??)?string('is-invalid', '')}"
               placeholder="Пароль"/>
        <#if passwordError??>
            <div class="invalid-feedback">
                ${passwordError}
            </div>
        </#if>
    </div>
  </div>
  <#if isRegisterForm>
          <div class="form-group row">
              <label class="col-sm-2 col-form-label">Password:</label>
              <div class="col-sm-6">
                  <input type="password" name="password2"
                         class="form-control ${(password2Error??)?string('is-invalid', '')}"
                         placeholder="Повторите пароль" />
                  <#if password2Error??>
                      <div class="invalid-feedback">
                          ${password2Error}
                      </div>
                  </#if>
              </div>
          </div>
      </#if>
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    <#if !isRegisterForm><a href="/registration">Add new user</a></#if>
    <button class="btn btn-primary" type="submit"><#if isRegisterForm>Добавить<#else>Войти</#if></button>
</form>
</#macro>

<#macro logout>

<form action="/logout" method="post">
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
  <button class="btn btn-primary" type="submit">Выйти</button>
</form>
</#macro>