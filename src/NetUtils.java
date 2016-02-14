
public class NetUtils {
    
    public static byte[] intToByte(int[] values){
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
    public static int[] byteToInt(byte[] byteArray){
        int[] intArray = new int[9];
        int currentByte =0;
        for (int currentInt = 0; currentInt < intArray.length;currentInt++){

            intArray[currentInt]=(int)byteArray[currentByte] + ((int)(byteArray[currentByte+1]) << 8);
            currentByte+=2;
        }
        return intArray;
    }
}
