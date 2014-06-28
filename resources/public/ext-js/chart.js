var drawChart = function(){
  var r = Raphael("chart", 720, 400)
  var c = r.path("M0,0").attr({fill: "none", "stroke-width": 2, "stroke-linecap": "round"})
  var l1 = r.path("M0 90H 800").attr({stroke: ["#ffffff"], fill: "none", "stroke-width": 2, "stroke-dasharray": ".", "stroke-linecap": "square"})
  var l2 = r.path("M0 290H 800").attr({stroke: ["#ffffff"], fill: "none", "stroke-width": 2, "stroke-dasharray": ".", "stroke-linecap": "square"})

  function randomDraw(){
    var dotsy = []
    var values = []
    var clr = []

    function randomPath(length, j) {
      var path = "",
      x = 0,
      y = 0;
      dotsy[j] = dotsy[j] || [];
      for (var i = 0; i < length; i++) {
        dotsy[j][i] = Math.round(Math.random() * 290);
        if (i) {
          x += 40;
          y = 300 - dotsy[j][i];
          path += "," + [x, y];
        } else {
          path += "M" + [0, (y = 240 - dotsy[j][i])] + "R";
        }
      }
      return path;
    }
    for (var i = 0; i < 3; i++) {
      values[i] = randomPath(25, i)
      clr[i] = Raphael.getColor('#ffffff')
    }
    c.attr({path: values[0], stroke: clr[0]})

    window.intersections = Raphael.pathIntersection(c,l1)
    //console.log(intersections)

    intersections.forEach(function(i){
      paper.circle (i.x, i.y, 3).attr({ fill: 'black' });
    })
  }

  randomDraw()
}
