<#import "parts/common.ftlh" as c>
<@c.page>
<div class="container">
    <form>
        <div class="form-group">
            <div class="col-sm-7">
                <div class="input-group date">
                    <input class="form-control" type="date" name="start" value=${dateStart}
                        max=${endYear} min=${startYear}>
                    <input class="form-control" type="date" name="end" value=${dateEnd}
                        max=${endYear} min=${startYear}>
                    <select id="person" name="person">
                        <#list persons as person>
                            <option value="${person}" <#if person == select>selected</#if>>
                                ${person}
                            </option>
                        </#list>
                    </select>
                    <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Выбрать</button>
                    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                </div>
            </div>
        </div>
    </form>

    <table class="table table-striped">
      <thead>
            <tr>
                <th scope="col">Дата</th>
                <th scope="col">День недели</th>
                <th scope="col">Смена</th>
           </tr>
       </thead>
    <#list repos as repo>
        <tr scope="row">
            <td>${repo.date}</td>
            <td>${repo.dayOfWeek}</td>
            <td>${repo.description}</td>
        </tr>
    <#else>
    Нет данных для отображения
    </#list>
    </table>
</div>
</@c.page>