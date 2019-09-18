package paintrebuild;

import javafx.scene.image.WritableImage;

public class Version {
    private WritableImage thisSnap, prevSnap, nextSnap;
    Version(WritableImage newSnapshot, WritableImage previous){
        thisSnap = newSnapshot;
        prevSnap = previous;
        nextSnap = null;
    }
    public WritableImage getSnapshot(){return thisSnap;}
    public WritableImage getPrevSnap(){return prevSnap;}
    public WritableImage getNextSnap(){return nextSnap;}
    public void setSnapshot(WritableImage newSnap){
        thisSnap=newSnap;
    }
    public void setPrevSnap(WritableImage newPrevSnap){
        prevSnap=newPrevSnap;
    }
    public void setNextSnap(WritableImage newNextSnap){
        nextSnap=newNextSnap;
    }
}
