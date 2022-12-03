<#macro login path isRegisterForm>

<form action="${path}" method="post">
  <div class="form-group row">
    <#if !isRegisterForm><a class="btn btn-outline-info" href="/registration">Зарегистрироваться</a></#if>
  </div>
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
    <button class="btn btn-outline-success my-2 my-sm-0" type="submit"><#if isRegisterForm>Добавить<#else>Войти</#if></button>
</form>
</#macro>

<#macro logout>

<form action="/logout" method="post">
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
  <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Выйти</button>
</form>
</#macro>