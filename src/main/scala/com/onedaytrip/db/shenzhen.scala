package com.onedaytrip.db

import com.mongodb.casbah.Imports._
import com.onedaytrip.db.poi.{shop, common}

/**
 * Created by tomek on 8/31/15.
 */
class shenzhen {

  def populate(col:MongoCollection) = {

  /**
   * <node id="1320185869" visible="true" version="3" changeset="17373022" timestamp="2013-08-16T15:01:21Z"
   * user="MarsmanRom" uid="44514" lat="22.5205351" lon="113.9191507">
  <tag k="alt_name" v="ChongShang Mall"/>
  <tag k="name" v="崇尚百货"/>
  <tag k="shop" v="supermarket"/>
 </node>
   */
  val point1 = common.node(22.5205351,113.9191507,
      common.info("","http://www.bocaraton.com/images/pompano_beach_shopping_center.jpg",""),
      shop.supermarket("崇尚百货"))

  /**
   * <node id="1670379618" visible="true" version="1" changeset="10943397"
   * timestamp="2012-03-11T14:23:51Z" user="alfredhuang" uid="577563"
   * lat="22.5299613" lon="113.9211662">
  <tag k="name" v="学府路人人乐超市"/>
  <tag k="shop" v="supermarket"/>
 </node>
   */
  val point2 = common.node(22.5299613, 113.9211662,
    common.info("","http://www.bocaraton.com/images/pompano_beach_shopping_center.jpg",""),
    shop.supermarket("学府路人人乐超市"))

  /**
   * <node id="2428981695" visible="true" version="1" changeset="17466996"
   * timestamp="2013-08-23T09:25:46Z" user="MarsmanRom" uid="44514" lat="22.5292094" lon="113.9253833">
  <tag k="shop" v="supermarket"/>
 </node>
   */
  val point3 = common.node(22.5292094, 113.9253833,
    common.info("","http://www.bocaraton.com/images/pompano_beach_shopping_center.jpg",""),
    shop.supermarket("unknown name"))

  /**
   * <node id="2428194227" visible="true" version="3" changeset="31922484"
   * timestamp="2015-06-12T14:22:20Z" user="Seaker" uid="2717018" lat="22.5227248" lon="113.9247756">
  <tag k="name" v="深圳书城南山城"/>
  <tag k="shop" v="books"/>
 </node>
   */
  val point4 = common.node(22.5227248, 113.9247756,
    common.info("","http://robot6.comicbookresources.com/wp-content/uploads/2011/05/01.jpg",""),
    shop.books("深圳书城南山城"))

  /**
   * <node id="2604006980" visible="true" version="2" changeset="19740291"
   * timestamp="2014-01-01T01:55:43Z" user="alfredhuang" uid="577563" lat="22.5266851" lon="113.9225083">
  <tag k="name" v="岁宝百货"/>
  <tag k="shop" v="mall"/>
 </node>
   */
  val point5 = common.node(22.5266851, 113.9225083,
    common.info("","http://www.bocaraton.com/images/pompano_beach_shopping_center.jpg",""),
    shop.mall("岁宝百货"))

  /**
   * <node id="2380714209" visible="true" version="2" changeset="17214759"
   * timestamp="2013-08-04T13:35:27Z" user="MarsmanRom" uid="44514" lat="22.5200509" lon="113.9327504">
  <tag k="name" v="Rainbow Shopping Centre"/>
  <tag k="shop" v="mall"/>
 </node>
   */
  val point6 = common.node(22.5200509, 113.9327504,
    common.info("","http://www.bocaraton.com/images/pompano_beach_shopping_center.jpg",""),
    shop.mall("Rainbow Shopping Centre"))

  /**
   * <node id="2431806905" visible="true" version="3" changeset="30400167"
   * timestamp="2015-04-22T09:58:59Z" user="deguoren34" uid="1222784" lat="22.5201839" lon="113.9340055">
  <tag k="name" v="家乐福 Carrefour"/>
  <tag k="shop" v="supermarket"/>
 </node>
   */
  val point7 = common.node(22.5201839, 113.9340055,
    common.info("","http://www.bocaraton.com/images/pompano_beach_shopping_center.jpg",""),
    shop.supermarket("家乐福 Carrefour"))

  /**
   * <node id="2448161265" visible="true" version="2" changeset="19231270"
   * timestamp="2013-12-02T11:35:45Z" user="MarsmanRom" uid="44514" lat="22.5200245" lon="113.9306979">
  <tag k="name" v="ÆON 永旺"/>
  <tag k="shop" v="supermarket"/>
 </node>
   */
  val point8 = common.node(22.5200245, 113.9306979,
    common.info("","http://www.bocaraton.com/images/pompano_beach_shopping_center.jpg",""),
    shop.supermarket("ÆON 永旺"))

  /**
   * <node id="2643548865" visible="true" version="1" changeset="20280976" timestamp="2014-01-30T10:37:11Z" user="kolyanoid" uid="1766553" lat="22.5217397" lon="113.9574364">
  <tag k="building" v="yes"/>
  <tag k="shop" v="kiosk"/>
 </node>
   */

  /**
   * <node id="1589775033" visible="true" version="1" changeset="10390269" timestamp="2012-01-14T17:39:42Z" user="Wrightbus" uid="261189" lat="22.5037746" lon="113.9168211">
  <tag k="addr:postcode" v="518067"/>
  <tag k="name" v="捷安特蛇口专卖店"/>
  <tag k="shop" v="bicycle"/>
 </node>
   */

  /**
   * <node id="2544665332" visible="true" version="1" changeset="19049516" timestamp="2013-11-22T09:53:01Z" user="MarsmanRom" uid="44514" lat="22.5486639" lon="113.9120756">
  <tag k="historic" v="monument"/>
 </node>
   */

  /**
   * <node id="3234395333" visible="true" version="1" changeset="27459488" timestamp="2014-12-14T14:32:44Z" user="Jonathan Tsai" uid="1903826" lat="22.5464405" lon="113.9183852">
  <tag k="historic" v="yes"/>
  <tag k="name" v="南頭古城東門"/>
 </node>
   */

  /**
   * <node id="2492226738" visible="true" version="1" changeset="18297131" timestamp="2013-10-11T12:51:34Z" user="MarsmanRom" uid="44514" lat="22.5278226" lon="113.9179993">
  <tag k="name" v="Welcome Inn"/>
  <tag k="tourism" v="hotel"/>
 </node>
   */


    col.insert(point1)
    col.insert(point2)
    col.insert(point3)
    col.insert(point4)
    col.insert(point5)
    col.insert(point6)
    col.insert(point7)
    col.insert(point8)

  }

}
