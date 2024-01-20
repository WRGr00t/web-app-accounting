<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>

<@c.page>
User editor
<form action="/user" method="post">
     <div class="input-group mb-3">
        <input type="text" name="username" value="${user.username}">
        <#if user.email??>
            <input type="text" name="email" value="${user.email}">
        <#else>
            <input type="text" name="email" placeholder="email">
        </#if>
            <#list roles as role>
                 <div class="input-group-text">
                      <div class="input-group-prepend">
                           <div class="form-check form-check-inline">
                               <input type="checkbox" aria-label="Checkbox for following text input" name="${role}" ${user.roles?seq_contains(role)?string("checked", "")}>${role}
                           </div>
                      </div>
                 </div>
            </#list>
     </div>
    <input type="hidden" name="userId" value="${user.id}">
    <input type="hidden" name="_csrf" value="${_csrf.token}">
    <button class="btn btn-primary" type="submit">Сохранить</button>
</form>
</@c.page>