package cn.shanghai.nicole.loopview;


/***
 *@date 创建时间 2019-08-20 13:34
 *@author 作者: BoXun.Zhao
 *@description  
 */
public class GetItemNameImpl implements IGetItemNameInterface {
    private String text = "";

    public GetItemNameImpl(String str) {
        text = str;
    }

    public GetItemNameImpl() {
    }

    @Override
    public String getShowString() {
        return text;
    }
}
