<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <title>InShiftApp</title>
  <link rel="icon" href="static/img/favicon.ico" type="image/x-icon">
  <link rel="stylesheet" href="static/monthstyle.css" />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.13.0/css/all.min.css" />
</head>

<body>
  <div class="container">
    <div class="calendar">
      <div class="month">
        <i class="fas fa-angle-left prev"></i>
        <div class="date">
          <h1></h1>
          <p></p>
          <form>
            <div class="form-control">
              <select id="person" name="person" onchange="getPerson(this.value)">
                <#list persons as person>
                  <option value="${person}" <#if person == select>selected</#if>>
                    ${person}
                  </option>
                </#list>
              </select>
              <input type="hidden" name="_csrf" value="${_csrf.token}"/>
            </div>
          </form>
        </div>
        <i class="fas fa-angle-right next"></i>
      </div>
      <div id="weekdays" class="weekdays">
      </div>
      <div class="days"></div>
    </div>
  </div>

  <script src="static/calendar.js"></script>
</body>

</html>