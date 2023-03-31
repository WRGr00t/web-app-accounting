<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <link rel="stylesheet" type="text/css" href="static/style.css">
    <title>InShiftApp</title>
    <link rel="icon" href="static/img/favicon.ico" type="image/x-icon">

</head>
<body>
<div class="head">
    <div>
        <a href="/">Назад</a>
    </div>
    <div class="desc">
        <div id="person"></div>
        <div id="year"></div>
    </div>
    <form>
        <div class="form-group">
            <div class="col-sm-7">
                <div class="input-group date">
                     <select id="person" name="person" onchange="getPerson(this.value)">
                          <#list persons as person>
                               <option value="${person}" <#if person == select>selected</#if>>
                                     ${person}
                               </option>
                          </#list>
                     </select>
                     <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                </div>
            </div>
        </div>
    </form>
</div>

    <div id="calendar" class="calendar">

    </div>
    <script type="text/javascript" src="static/script.js"></script>

</body>
</html>