<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>

<@c.page>
User editor
<form action="/user" method="post">
    <input type="text" name="username" value="${user.username}">
        <#list roles as role>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <div class="input-group-text">
                  <input type="checkbox" aria-label="Checkbox for following text input" name="${role}" ${user.roles?seq_contains(role)?string("checked", "")}>${role}
                </div>
              </div>
            </div>
        </#list>
    <input type="hidden" value="${user.id}" name="userId">
    <input type="hidden" value="${_csrf}" name="_csrf">
    <button type="submit">Save</button>
</form>
</@c.page>