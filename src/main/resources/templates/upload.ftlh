<#import "parts/common.ftlh" as c>

<@c.page>
<form class="row g-3" method="post" enctype="multipart/form-data">
    <div class="col-auto">
      <label for="formFile" class="form-label">Выберите CSV-файл для загрузки</label>
    </div>
    <div class="col-auto">
      <input class="form-control" type="file" id="formFile" name="file" accept="application/csvm+json">
      <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    </div>
    <div class="col-auto">
      <button type="submit" class="btn btn-primary mb-3">Загрузить</button>
    </div>
</form>
    <div>
        ${msg?ifExists}
    </div>

</@c.page>