<#import "parts/common.ftlh" as c>

<@c.page>
<h5>${username} (редактирование профиля)</h5>
${message?ifExists}
<form method="post">
  <div class="form-group row">
    <label class="col-sm-2 col-form-label"> User Name :</label>
    <div class="col-sm-6">
        <input type="text" name="username" class="form-control" placeholder="${username}"/>
    </div>
  </div>
  <div class="form-group row">
    <label class="col-sm-2 col-form-label"> Password: </label>
    <div class="col-sm-6">
        <input type="password" name="password" class="form-control" placeholder="Пароль"/>
    </div>
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
  </div>
    <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Сохранить</button>
  </div>
</form>
</@c.page>