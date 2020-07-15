/**
 * Created by Administrator on 2016/5/4.
 */
function CalculateFreight(){
  var distance = document.getElementsByName('mall_logistics_info.form.total_distance')[0].value;
  var unit_freight = document.getElementsByName('mall_logistics_info.form.parame')[0].value;
  document.getElementsByName('mall_logistics_info.form.freight')[0].value = distance*unit_freight;
}

