
public class Utilities {
    
    public byte[] intToByte(int[] values){
        int valueCount=0;
        byte[] bytes = new byte[(values.length*2)];
        for (int i=0;i < bytes.length;i++){
            if (i%2==0){
                bytes[i]=(byte)(values[valueCount] & 0xff);
            }
            else {
                bytes[i]=(byte)(((values[valueCount] & 0xff00) >> 8));
                valueCount++;
            }
        }
        return bytes;
        
    }
}
