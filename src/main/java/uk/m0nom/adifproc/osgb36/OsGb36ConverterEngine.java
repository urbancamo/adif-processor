package uk.m0nom.adifproc.osgb36;

/**
 * Decompiled from Swing application: The_Java_British_National_Grid_Converter
 * Author: Jan Herold (yzle at gmx dot de)
 * Might look weird, but I've tried not to tamper with the converter code at all, hence the wrapper class.
 */
public class OsGb36ConverterEngine {
    int accuracystringindex;
    String[][] MapSquare;
    StringBuffer bngoldcoordinatesstringbuffer;
    StringBuffer bngoldmapquarestringbuffer;
    StringBuffer bngoldcoordinatesnumbersstringbuffer;
    StringBuffer bngoldheightstringbuffer;
    StringBuffer bngoldeastingsstringbuffer;
    StringBuffer bngoldnorthingsstringbuffer;
    StringBuffer bngeastingscoordinatesstringbuffer;
    StringBuffer bngnorthingscoordinatesstringbuffer;
    StringBuffer longitudedecosgb36stringbuffer;
    StringBuffer latitudedecosgb36stringbuffer;
    StringBuffer longitudedegosgb36stringbuffer;
    StringBuffer longitudeminosgb36stringbuffer;
    StringBuffer longitudesecosgb36stringbuffer;
    StringBuffer latitudedegosgb36stringbuffer;
    StringBuffer latitudeminosgb36stringbuffer;
    StringBuffer latitudesecosgb36stringbuffer;
    StringBuffer cartesianxosgb36stringbuffer;
    StringBuffer cartesianyosgb36stringbuffer;
    StringBuffer cartesianzosgb36stringbuffer;
    StringBuffer cartesianxwgs84stringbuffer;
    StringBuffer cartesianywgs84stringbuffer;
    StringBuffer cartesianzwgs84stringbuffer;
    StringBuffer longitudedecwgs84stringbuffer;
    StringBuffer latitudedecwgs84stringbuffer;
    StringBuffer heightdecwgs84stringbuffer;
    StringBuffer longitudedegwgs84stringbuffer;
    StringBuffer longitudeminwgs84stringbuffer;
    StringBuffer longitudesecwgs84stringbuffer;
    StringBuffer latitudedegwgs84stringbuffer;
    StringBuffer latitudeminwgs84stringbuffer;
    StringBuffer latitudesecwgs84stringbuffer;
    StringBuffer longitudedbxstringbuffer;
    StringBuffer latitudedbxstringbuffer;
    StringBuffer zerofillstringbuffer;
    StringBuffer bngoldeastingscoordinatesstringbuffer;
    StringBuffer bngoldnorthingscoordinatesstringbuffer;
    StringBuffer convergenceosgb36stringbuffer;
    StringBuffer localscalefactorosgb36stringbuffer;
    StringBuffer messagestextareastringbuffer;
    int bngoldcoordinatesstringbufferlength;
    double bngeastingscoordinates;
    double bngnorthingscoordinates;
    int msi1;
    int msi2;
    int index1;
    int index2;
    boolean msnf;
    double bngoldheight;
    int zerofillstringbufferlength;
    double ellipsoid_airy1830_a;
    double ellipsoid_airy1830_af;
    double ellipsoid_airy1830_bf;
    double n_ab_airy;
    double eccentricity2_airy;
    double ellipsoid_wgs84_a;
    double eccentricity2_wgs84;
    double projection_bng_f0;
    double trueorigin_N0;
    double trueorigin_E0;
    double phi0rad;
    double meridionalarc;
    double approx;
    double phinew;
    double phiold;
    double phi1;
    double phi2;
    double p2mp1;
    double p2pp1;
    double ny;
    double rho;
    double eta2;
    double tanphinew;
    double VII;
    double VIII;
    double IX;
    double bngtrueeastingscoordinates;
    double phirad;
    double phi;
    double phiosgb36;
    double secphinew;
    double X;
    double XI;
    double XII;
    double XIIA;
    double lambdarad;
    double lambda;
    double lambdaosgb36;
    double lambda0;
    double lambda0rad;
    double decdegreesdegrees;
    double decdegreesminutes;
    double decdegreesseconds;
    double phi1rad;
    double phi2rad;
    double longitudedecosgb36;
    double latitudedecosgb36;
    double lambda1rad;
    double lambda2rad;
    double I;
    double II;
    double III;
    double IIIA;
    double IV;
    double V;
    double VI;
    double Ptruelambda;
    double heightellipsoid;
    double cartesianxosgb36;
    double cartesianyosgb36;
    double cartesianzosgb36;
    double pdiagxy;
    double helmrx;
    double helmry;
    double helmrz;
    double helms;
    double helmtx;
    double helmty;
    double helmtz;
    double helmx;
    double helmy;
    double helmz;
    double heightdecwgs84;
    double cartesianxwgs84;
    double cartesianywgs84;
    double cartesianzwgs84;
    double longitudedecwgs84;
    double latitudedecwgs84;
    double dbxconst;
    long longitudedbx;
    long latitudedbx;
    double XIII;
    double XIV;
    double XV;
    double XIX;
    double XX;
    double convergenceosgb36;
    double localscalefactorosgb36;
    double longitudedecosgb36rad;
    double latitudedecosgb36rad;
    boolean stopcalculating;
    double bngoldeastingscoordinates;
    double bngoldnorthingscoordinates;
    String[] accuracyunitsstring = new String[]{"1 m", "10 m", "100 m", "1 km", "10 km", "100 km"};

    public OsGb36ConverterEngine() {
        this.accuracystringindex = 0;
        this.MapSquare = new String[][]{{"SV", "SW", "SX", "SY", "SZ", "TV", "TW"}, {"SQ", "SR", "SS", "ST", "SU", "TQ", "TR"}, {"SL", "SM", "SN", "SO", "SP", "TL", "TM"}, {"SF", "SG", "SH", "SJ", "SK", "TF", "TG"}, {"SA", "SB", "SC", "SD", "SE", "TA", "TB"}, {"NV", "NW", "NX", "NY", "NZ", "OV", "OW"}, {"NQ", "NR", "NS", "NT", "NU", "OQ", "OR"}, {"NL", "NM", "NN", "NO", "NP", "OL", "OM"}, {"NF", "NG", "NH", "NJ", "NK", "OF", "OG"}, {"NA", "NB", "NC", "ND", "NE", "OA", "OB"}, {"HV", "HW", "HX", "HY", "HZ", "JV", "JW"}, {"HQ", "HR", "HS", "HT", "HU", "JQ", "JR"}, {"HL", "HM", "HN", "HO", "HP", "JL", "JM"}};
        this.bngoldcoordinatesstringbuffer = new StringBuffer(0);
        this.bngoldmapquarestringbuffer = new StringBuffer(0);
        this.bngoldcoordinatesnumbersstringbuffer = new StringBuffer(0);
        this.bngoldheightstringbuffer = new StringBuffer(0);
        this.bngoldeastingsstringbuffer = new StringBuffer(0);
        this.bngoldnorthingsstringbuffer = new StringBuffer(0);
        this.bngeastingscoordinatesstringbuffer = new StringBuffer(0);
        this.bngnorthingscoordinatesstringbuffer = new StringBuffer(0);
        this.longitudedecosgb36stringbuffer = new StringBuffer(0);
        this.latitudedecosgb36stringbuffer = new StringBuffer(0);
        this.longitudedegosgb36stringbuffer = new StringBuffer(0);
        this.longitudeminosgb36stringbuffer = new StringBuffer(0);
        this.longitudesecosgb36stringbuffer = new StringBuffer(0);
        this.latitudedegosgb36stringbuffer = new StringBuffer(0);
        this.latitudeminosgb36stringbuffer = new StringBuffer(0);
        this.latitudesecosgb36stringbuffer = new StringBuffer(0);
        this.cartesianxosgb36stringbuffer = new StringBuffer(0);
        this.cartesianyosgb36stringbuffer = new StringBuffer(0);
        this.cartesianzosgb36stringbuffer = new StringBuffer(0);
        this.cartesianxwgs84stringbuffer = new StringBuffer(0);
        this.cartesianywgs84stringbuffer = new StringBuffer(0);
        this.cartesianzwgs84stringbuffer = new StringBuffer(0);
        this.longitudedecwgs84stringbuffer = new StringBuffer(0);
        this.latitudedecwgs84stringbuffer = new StringBuffer(0);
        this.heightdecwgs84stringbuffer = new StringBuffer(0);
        this.longitudedegwgs84stringbuffer = new StringBuffer(0);
        this.longitudeminwgs84stringbuffer = new StringBuffer(0);
        this.longitudesecwgs84stringbuffer = new StringBuffer(0);
        this.latitudedegwgs84stringbuffer = new StringBuffer(0);
        this.latitudeminwgs84stringbuffer = new StringBuffer(0);
        this.latitudesecwgs84stringbuffer = new StringBuffer(0);
        this.longitudedbxstringbuffer = new StringBuffer(0);
        this.latitudedbxstringbuffer = new StringBuffer(0);
        this.zerofillstringbuffer = new StringBuffer(0);
        this.bngoldeastingscoordinatesstringbuffer = new StringBuffer(0);
        this.bngoldnorthingscoordinatesstringbuffer = new StringBuffer(0);
        this.convergenceosgb36stringbuffer = new StringBuffer(0);
        this.localscalefactorosgb36stringbuffer = new StringBuffer(0);
        this.messagestextareastringbuffer = new StringBuffer();
        this.ellipsoid_airy1830_a = 6377563.396D;
        this.ellipsoid_airy1830_af = 6375020.480988971D;
        this.ellipsoid_airy1830_bf = 6353722.490487913D;
        this.n_ab_airy = 0.0016732202503250873D;
        this.eccentricity2_airy = 0.0066705397615975295D;
        this.ellipsoid_wgs84_a = 6378137.0D;
        this.eccentricity2_wgs84 = 0.006694380035512791D;
        this.projection_bng_f0 = 0.9996012717D;
        this.trueorigin_N0 = -100000.0D;
        this.trueorigin_E0 = 400000.0D;
        this.phi0rad = 0.8552113334772214D;
        this.lambda0 = -2.0D;
        this.lambda0rad = -0.03490658503988659D;
        this.helmrx = -7.281901490265231E-7D;
        this.helmry = -1.1974897923405538E-6D;
        this.helmrz = -4.082616008623403E-6D;
        this.helms = -2.04894E-5D;
        this.helmtx = 446.448D;
        this.helmty = -125.157D;
        this.helmtz = 542.06D;
        this.dbxconst = 1.1930464711111112E7D;
    }

    void deletestringbuffers() {
        this.bngoldcoordinatesnumbersstringbuffer.setLength(0);
        this.bngoldcoordinatesstringbuffer.setLength(0);
        this.bngoldmapquarestringbuffer.setLength(0);
        this.bngoldheightstringbuffer.setLength(0);
        this.bngoldeastingsstringbuffer.setLength(0);
        this.bngoldnorthingsstringbuffer.setLength(0);
        this.bngeastingscoordinatesstringbuffer.setLength(0);
        this.bngnorthingscoordinatesstringbuffer.setLength(0);
        this.bngoldeastingscoordinatesstringbuffer.setLength(0);
        this.bngoldnorthingscoordinatesstringbuffer.setLength(0);
        this.longitudedecosgb36stringbuffer.setLength(0);
        this.latitudedecosgb36stringbuffer.setLength(0);
        this.longitudedegosgb36stringbuffer.setLength(0);
        this.longitudeminosgb36stringbuffer.setLength(0);
        this.longitudesecosgb36stringbuffer.setLength(0);
        this.latitudedegosgb36stringbuffer.setLength(0);
        this.latitudeminosgb36stringbuffer.setLength(0);
        this.latitudesecosgb36stringbuffer.setLength(0);
        this.cartesianxosgb36stringbuffer.setLength(0);
        this.cartesianyosgb36stringbuffer.setLength(0);
        this.cartesianzosgb36stringbuffer.setLength(0);
        this.cartesianxwgs84stringbuffer.setLength(0);
        this.cartesianywgs84stringbuffer.setLength(0);
        this.cartesianzwgs84stringbuffer.setLength(0);
        this.longitudedecwgs84stringbuffer.setLength(0);
        this.latitudedecwgs84stringbuffer.setLength(0);
        this.heightdecwgs84stringbuffer.setLength(0);
        this.longitudedegwgs84stringbuffer.setLength(0);
        this.longitudeminwgs84stringbuffer.setLength(0);
        this.longitudesecwgs84stringbuffer.setLength(0);
        this.latitudedegwgs84stringbuffer.setLength(0);
        this.latitudeminwgs84stringbuffer.setLength(0);
        this.latitudesecwgs84stringbuffer.setLength(0);
        this.longitudedbxstringbuffer.setLength(0);
        this.latitudedbxstringbuffer.setLength(0);
        this.convergenceosgb36stringbuffer.setLength(0);
        this.localscalefactorosgb36stringbuffer.setLength(0);
        this.zerofillstringbuffer.setLength(0);
        this.stopcalculating = false;
    }


    void showerrormessage(String var1) {
        this.messagestextareastringbuffer.append("\n");
        this.messagestextareastringbuffer.append(var1);
        //this.messagestextarea.setText(this.messagestextareastringbuffer.toString());
        this.stopcalculating = true;
    }

    void convertoldbngtonewbng() {
        this.bngoldcoordinatesstringbufferlength = this.bngoldcoordinatesstringbuffer.length();
        if (this.bngoldcoordinatesstringbufferlength == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - coordinates must not be empty!");
        } else if (this.bngoldcoordinatesstringbufferlength % 2 != 0) {
            this.showerrormessage("Number of digits in BNG-coordinates must be even!");
        } else if (this.bngoldheightstringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - height (m) must not be empty!");
        } else {
            this.bngoldmapquarestringbuffer.append(this.bngoldcoordinatesstringbuffer.substring(0, 2));
            this.bngoldcoordinatesnumbersstringbuffer.append(this.bngoldcoordinatesstringbuffer.substring(2));
            if (this.bngoldcoordinatesnumbersstringbuffer.length() != 0) {
                this.bngoldeastingsstringbuffer.append(this.bngoldcoordinatesnumbersstringbuffer.substring(0, this.bngoldcoordinatesnumbersstringbuffer.length() / 2));
                this.bngoldnorthingsstringbuffer.append(this.bngoldcoordinatesnumbersstringbuffer.substring(this.bngoldcoordinatesnumbersstringbuffer.length() / 2));
            } else {
                this.bngoldeastingsstringbuffer.append("0");
                this.bngoldnorthingsstringbuffer.append("0");
            }

            this.index1 = -1;
            this.index2 = -1;
            this.msi1 = -1;
            this.msi2 = -1;

            for(this.msnf = true; this.index1 < 6 && this.msnf; this.index2 = -1) {
                ++this.index1;

                while(this.index2 < 12 && this.msnf) {
                    ++this.index2;
                    if (this.MapSquare[this.index2][this.index1].equals(this.bngoldmapquarestringbuffer.toString().toUpperCase())) {
                        this.msnf = false;
                        this.msi1 = this.index1;
                        this.msi2 = this.index2;
                    }
                }
            }

            if (this.msi1 != -1 && this.msi2 != -1) {
                this.msi1 *= 100000;
                this.msi2 *= 100000;
                this.bngeastingscoordinates = Double.parseDouble(this.bngoldeastingsstringbuffer.toString());
                this.bngeastingscoordinates *= StrictMath.pow(10.0D, 5.0D - (double)this.bngoldeastingsstringbuffer.length());
                this.bngeastingscoordinates += (double)this.msi1;
                this.bngeastingscoordinatesstringbuffer.setLength(0);
                this.bngeastingscoordinatesstringbuffer.append(String.valueOf(this.bngeastingscoordinates));
                this.bngnorthingscoordinates = Double.parseDouble(this.bngoldnorthingsstringbuffer.toString());
                this.bngnorthingscoordinates *= StrictMath.pow(10.0D, 5.0D - (double)this.bngoldnorthingsstringbuffer.length());
                this.bngnorthingscoordinates += (double)this.msi2;
                this.bngnorthingscoordinatesstringbuffer.setLength(0);
                this.bngnorthingscoordinatesstringbuffer.append(String.valueOf(this.bngnorthingscoordinates));
                this.bngoldheight = Double.parseDouble(this.bngoldheightstringbuffer.toString());
            } else {
                this.showerrormessage("Unknown Map-Square!");
            }
        }

    }

    private String fillWithZeros(double var1, int var3) {
        this.zerofillstringbuffer.setLength(0);
        this.zerofillstringbuffer.append(String.valueOf((int)var1));
        this.zerofillstringbufferlength = this.zerofillstringbuffer.length();
        if (this.zerofillstringbufferlength == var3) {
            return this.zerofillstringbuffer.toString();
        } else if (this.zerofillstringbufferlength >= var3) {
            if (this.zerofillstringbufferlength > var3) {
                this.zerofillstringbuffer.delete(var3, this.zerofillstringbufferlength);
                return this.zerofillstringbuffer.toString();
            } else {
                return this.zerofillstringbuffer.toString();
            }
        } else {
            for(int var4 = 0; var4 < var3 - this.zerofillstringbufferlength; ++var4) {
                this.zerofillstringbuffer.insert(0, "0");
            }

            return this.zerofillstringbuffer.toString();
        }
    }


    void convertnewbngtooldbng() {
        if (this.bngeastingscoordinatesstringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - eastings (m) must not be empty!");
        } else if (this.bngnorthingscoordinatesstringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - northings (m) must not be empty!");
        } else if (this.bngoldheightstringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - height (m) must not be empty!");
        } else {
            this.bngeastingscoordinates = Double.parseDouble(this.bngeastingscoordinatesstringbuffer.toString());
            this.bngnorthingscoordinates = Double.parseDouble(this.bngnorthingscoordinatesstringbuffer.toString());
            if (!(this.bngeastingscoordinates >= 700000.0D) && !(this.bngeastingscoordinates < 0.0D)) {
                if (!(this.bngnorthingscoordinates >= 1300000.0D) && !(this.bngnorthingscoordinates < 0.0D)) {
                    this.msi1 = (int)(StrictMath.floor(this.bngeastingscoordinates) / 100000.0D);
                    this.msi2 = (int)(StrictMath.floor(this.bngnorthingscoordinates) / 100000.0D);
                    this.bngoldeastingscoordinates = this.bngeastingscoordinates - (double)this.msi1 * 100000.0D;
                    this.bngoldnorthingscoordinates = this.bngnorthingscoordinates - (double)this.msi2 * 100000.0D;
                    switch(this.accuracystringindex) {
                        case 0:
                            this.bngoldeastingscoordinates = StrictMath.floor(this.bngoldeastingscoordinates);
                            this.bngoldnorthingscoordinates = StrictMath.floor(this.bngoldnorthingscoordinates);
                            this.bngoldeastingscoordinatesstringbuffer.append(this.fillWithZeros(this.bngoldeastingscoordinates, 5));
                            this.bngoldnorthingscoordinatesstringbuffer.append(this.fillWithZeros(this.bngoldnorthingscoordinates, 5));
                            break;
                        case 1:
                            this.bngoldeastingscoordinates = StrictMath.floor(this.bngoldeastingscoordinates / 10.0D);
                            this.bngoldnorthingscoordinates = StrictMath.floor(this.bngoldnorthingscoordinates / 10.0D);
                            this.bngoldeastingscoordinatesstringbuffer.append(this.fillWithZeros(this.bngoldeastingscoordinates, 4));
                            this.bngoldnorthingscoordinatesstringbuffer.append(this.fillWithZeros(this.bngoldnorthingscoordinates, 4));
                            break;
                        case 2:
                            this.bngoldeastingscoordinates = StrictMath.floor(this.bngoldeastingscoordinates / 100.0D);
                            this.bngoldnorthingscoordinates = StrictMath.floor(this.bngoldnorthingscoordinates / 100.0D);
                            this.bngoldeastingscoordinatesstringbuffer.append(this.fillWithZeros(this.bngoldeastingscoordinates, 3));
                            this.bngoldnorthingscoordinatesstringbuffer.append(this.fillWithZeros(this.bngoldnorthingscoordinates, 3));
                            break;
                        case 3:
                            this.bngoldeastingscoordinates = StrictMath.floor(this.bngoldeastingscoordinates / 1000.0D);
                            this.bngoldnorthingscoordinates = StrictMath.floor(this.bngoldnorthingscoordinates / 1000.0D);
                            this.bngoldeastingscoordinatesstringbuffer.append(this.fillWithZeros(this.bngoldeastingscoordinates, 2));
                            this.bngoldnorthingscoordinatesstringbuffer.append(this.fillWithZeros(this.bngoldnorthingscoordinates, 2));
                            break;
                        case 4:
                            this.bngoldeastingscoordinates = StrictMath.floor(this.bngoldeastingscoordinates / 10000.0D);
                            this.bngoldnorthingscoordinates = StrictMath.floor(this.bngoldnorthingscoordinates / 10000.0D);
                            this.bngoldeastingscoordinatesstringbuffer.append(String.valueOf((int)this.bngoldeastingscoordinates));
                            this.bngoldnorthingscoordinatesstringbuffer.append(String.valueOf((int)this.bngoldnorthingscoordinates));
                        case 5:
                    }

                    this.bngoldcoordinatesstringbuffer.setLength(0);
                    this.bngoldcoordinatesstringbuffer.append(this.MapSquare[this.msi2][this.msi1]).append(this.bngoldeastingscoordinatesstringbuffer.toString()).append(this.bngoldnorthingscoordinatesstringbuffer.toString());
                    this.bngoldheight = Double.parseDouble(this.bngoldheightstringbuffer.toString());
                    this.bngoldheightstringbuffer.setLength(0);
                    this.bngoldheightstringbuffer.append(String.valueOf(this.bngoldheight));
                } else {
                    this.showerrormessage("Northings not in defined grid!");
                    this.bngoldcoordinatesstringbuffer.setLength(0);
                }
            } else {
                this.showerrormessage("Eastings not in defined grid!");
                this.bngoldcoordinatesstringbuffer.setLength(0);
            }
        }

    }

    private double calculatemeridionalarc(double var1, double var3, double var5, double var7) {
        return var1 * ((1.0D + var3 + 1.25D * StrictMath.pow(var3, 2.0D) + 1.25D * StrictMath.pow(var3, 3.0D)) * var7 - (3.0D * var3 + 3.0D * StrictMath.pow(var3, 2.0D) + 2.625D * StrictMath.pow(var3, 3.0D)) * StrictMath.sin(var7) * StrictMath.cos(var5) + (1.875D * StrictMath.pow(var3, 2.0D) + 1.875D * StrictMath.pow(var3, 3.0D)) * StrictMath.sin(2.0D * var7) * StrictMath.cos(2.0D * var5) - 1.4583333333333333D * StrictMath.pow(var3, 3.0D) * StrictMath.sin(3.0D * var7) * StrictMath.cos(3.0D * var5));
    }

    private double calculateny(double var1, double var3, double var5) {
        return var1 / StrictMath.sqrt(1.0D - var5 * StrictMath.pow(StrictMath.sin(var3), 2.0D));
    }

    private double calculaterho(double var1, double var3) {
        return var1 * (1.0D - this.eccentricity2_airy) / (1.0D - this.eccentricity2_airy * StrictMath.pow(StrictMath.sin(var3), 2.0D));
    }

    private double calculateeta2(double var1, double var3) {
        return var1 / var3 - 1.0D;
    }

    public void convertnewbngtolonglatosgb36() {
        if (this.bngeastingscoordinatesstringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - eastings (m) must not be empty!");
        } else if (this.bngnorthingscoordinatesstringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - northings (m) must not be empty!");
        } else if (this.bngoldheightstringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - height (m) must not be empty!");
        } else {
            this.bngeastingscoordinates = Double.parseDouble(this.bngeastingscoordinatesstringbuffer.toString());
            this.bngnorthingscoordinates = Double.parseDouble(this.bngnorthingscoordinatesstringbuffer.toString());
            this.meridionalarc = 0.0D;
            this.phiold = this.phi0rad;

            for(int var1 = 0; var1 <= 10000; ++var1) {
                this.approx = this.bngnorthingscoordinates - this.trueorigin_N0 - this.meridionalarc;
                this.phinew = this.approx / this.ellipsoid_airy1830_af + this.phiold;
                this.phi1 = this.phi0rad;
                this.phi2 = this.phinew;
                this.p2mp1 = this.phi2 - this.phi1;
                this.p2pp1 = this.phi2 + this.phi1;
                this.meridionalarc = this.calculatemeridionalarc(this.ellipsoid_airy1830_bf, this.n_ab_airy, this.p2pp1, this.p2mp1);
                this.phiold = this.phinew;
                if (StrictMath.abs(this.approx) < 1.0E-12D) {
                    break;
                }
            }

            this.ny = this.calculateny(this.ellipsoid_airy1830_af, this.phinew, this.eccentricity2_airy);
            this.rho = this.calculaterho(this.ny, this.phinew);
            this.eta2 = this.calculateeta2(this.ny, this.rho);
            this.tanphinew = StrictMath.tan(this.phinew);
            this.VII = this.tanphinew / 2.0D / this.rho / this.ny;
            this.VIII = this.tanphinew / 24.0D / this.rho / StrictMath.pow(this.ny, 3.0D) * (5.0D + 3.0D * StrictMath.pow(this.tanphinew, 2.0D) + this.eta2 - 9.0D * StrictMath.pow(StrictMath.tan(this.phinew), 2.0D) * this.eta2);
            this.IX = this.tanphinew / 720.0D / this.rho / StrictMath.pow(this.ny, 5.0D) * (61.0D + 90.0D * StrictMath.pow(this.tanphinew, 2.0D) + 45.0D * StrictMath.pow(this.tanphinew, 4.0D));
            this.bngtrueeastingscoordinates = this.bngeastingscoordinates - this.trueorigin_E0;
            this.phirad = this.phinew - StrictMath.pow(this.bngtrueeastingscoordinates, 2.0D) * this.VII + StrictMath.pow(this.bngtrueeastingscoordinates, 4.0D) * this.VIII - StrictMath.pow(this.bngtrueeastingscoordinates, 6.0D) * this.IX;
            this.phiosgb36 = StrictMath.toDegrees(this.phirad);
            this.latitudedecosgb36stringbuffer.setLength(0);
            this.latitudedecosgb36stringbuffer.append(String.valueOf(this.phiosgb36));
            this.secphinew = 1.0D / StrictMath.cos(this.phinew);
            this.X = this.secphinew / this.ny;
            this.XI = this.secphinew / 6.0D / StrictMath.pow(this.ny, 3.0D) * (this.ny / this.rho + 2.0D * StrictMath.pow(this.tanphinew, 2.0D));
            this.XII = this.secphinew / 120.0D / StrictMath.pow(this.ny, 5.0D) * (5.0D + 28.0D * StrictMath.pow(this.tanphinew, 2.0D) + 24.0D * StrictMath.pow(this.tanphinew, 4.0D));
            this.XIIA = this.secphinew / 5040.0D / StrictMath.pow(this.ny, 7.0D) * (61.0D + 662.0D * StrictMath.pow(this.tanphinew, 2.0D) + 1320.0D * StrictMath.pow(this.tanphinew, 4.0D) + 720.0D * StrictMath.pow(this.tanphinew, 6.0D));
            this.lambdarad = this.bngtrueeastingscoordinates * this.X - StrictMath.pow(this.bngtrueeastingscoordinates, 3.0D) * this.XI + StrictMath.pow(this.bngtrueeastingscoordinates, 5.0D) * this.XII - StrictMath.pow(this.bngtrueeastingscoordinates, 7.0D) * this.XIIA;
            this.lambdaosgb36 = this.lambda0 + StrictMath.toDegrees(this.lambdarad);
            this.longitudedecosgb36stringbuffer.setLength(0);
            this.longitudedecosgb36stringbuffer.append(String.valueOf(this.lambdaosgb36));
            this.bngoldheight = Double.parseDouble(this.bngoldheightstringbuffer.toString());
            this.bngoldheightstringbuffer.setLength(0);
            this.bngoldheightstringbuffer.append(String.valueOf(this.bngoldheight));
        }

    }

    public void convertlonglatosgb36tonewbng() {
        if (this.longitudedecosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - longitude λ (deg) must not be empty!");
        } else if (this.latitudedecosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - latitude φ (deg) must not be empty!");
        } else if (this.bngoldheightstringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - height (m) must not be empty!");
        } else {
            this.longitudedecosgb36 = Double.parseDouble(this.longitudedecosgb36stringbuffer.toString());
            this.latitudedecosgb36 = Double.parseDouble(this.latitudedecosgb36stringbuffer.toString());
            this.phi2rad = StrictMath.toRadians(this.latitudedecosgb36);
            this.phi1rad = this.phi0rad;
            this.p2mp1 = this.phi2rad - this.phi1rad;
            this.p2pp1 = this.phi2rad + this.phi1rad;
            this.meridionalarc = this.calculatemeridionalarc(this.ellipsoid_airy1830_bf, this.n_ab_airy, this.p2pp1, this.p2mp1);
            this.lambda1rad = this.lambda0rad;
            this.lambda2rad = StrictMath.toRadians(this.longitudedecosgb36);
            this.I = this.meridionalarc + this.trueorigin_N0;
            this.ny = this.calculateny(this.ellipsoid_airy1830_af, this.phi2rad, this.eccentricity2_airy);
            this.rho = this.calculaterho(this.ny, this.phi2rad);
            this.eta2 = this.calculateeta2(this.ny, this.rho);
            this.Ptruelambda = this.lambda2rad - this.lambda1rad;
            this.II = this.ny / 2.0D * StrictMath.sin(this.phi2rad) * StrictMath.cos(this.phi2rad);
            this.III = this.ny / 24.0D * StrictMath.sin(this.phi2rad) * StrictMath.pow(StrictMath.cos(this.phi2rad), 3.0D) * (5.0D - StrictMath.pow(StrictMath.tan(this.phi2rad), 2.0D) + 9.0D * this.eta2);
            this.IIIA = this.ny / 720.0D * StrictMath.sin(this.phi2rad) * StrictMath.pow(StrictMath.cos(this.phi2rad), 5.0D) * (61.0D - 58.0D * StrictMath.pow(StrictMath.tan(this.phi2rad), 2.0D) + StrictMath.pow(StrictMath.tan(this.phi2rad), 4.0D));
            this.bngnorthingscoordinates = this.I + StrictMath.pow(this.Ptruelambda, 2.0D) * this.II + StrictMath.pow(this.Ptruelambda, 4.0D) * this.III + StrictMath.pow(this.Ptruelambda, 6.0D) * this.IIIA;
            this.bngnorthingscoordinatesstringbuffer.setLength(0);
            this.bngnorthingscoordinatesstringbuffer.append(String.valueOf(this.bngnorthingscoordinates));
            this.IV = this.ny * StrictMath.cos(this.phi2rad);
            this.V = this.ny / 6.0D * StrictMath.pow(StrictMath.cos(this.phi2rad), 3.0D) * (this.ny / this.rho - StrictMath.pow(StrictMath.tan(this.phi2rad), 2.0D));
            this.VI = this.ny / 120.0D * StrictMath.pow(StrictMath.cos(this.phi2rad), 5.0D) * (5.0D - 18.0D * StrictMath.pow(StrictMath.tan(this.phi2rad), 2.0D) + StrictMath.pow(StrictMath.tan(this.phi2rad), 4.0D) + 14.0D * this.eta2 - 58.0D * this.eta2 * StrictMath.pow(StrictMath.tan(this.phi2rad), 2.0D));
            this.bngeastingscoordinates = this.trueorigin_E0 + this.Ptruelambda * this.IV + StrictMath.pow(this.Ptruelambda, 3.0D) * this.V + StrictMath.pow(this.Ptruelambda, 5.0D) * this.VI;
            this.bngeastingscoordinatesstringbuffer.setLength(0);
            this.bngeastingscoordinatesstringbuffer.append(String.valueOf(this.bngeastingscoordinates));
            this.bngoldheight = Double.parseDouble(this.bngoldheightstringbuffer.toString());
            this.bngoldheightstringbuffer.setLength(0);
            this.bngoldheightstringbuffer.append(String.valueOf(this.bngoldheight));
        }

    }

    public double convertstringtodec(double var1, double var3, double var5) {
        return var1 < 0.0D ? var1 - var3 / 60.0D - var5 / 3600.0D : var1 + var3 / 60.0D + var5 / 3600.0D;
    }

    public void convertlonglatosgb36tolonglatdecosgb36() {
        if (this.longitudedegosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - longitude λ (deg) must not be empty!");
        } else if (this.longitudeminosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - longitude φ (min) must not be empty!");
        } else if (this.longitudesecosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - longitude φ (sec) must not be empty!");
        } else if (this.latitudedegosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - latitude φ (deg) must not be empty!");
        } else if (this.latitudeminosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - latitude φ (min) must not be empty!");
        } else if (this.latitudesecosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - latitude φ (sec) must not be empty!");
        } else if (this.bngoldheightstringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - height (m) must not be empty!");
        } else {
            this.lambda = this.convertstringtodec(Double.parseDouble(this.longitudedegosgb36stringbuffer.toString()), Double.parseDouble(this.longitudeminosgb36stringbuffer.toString()), Double.parseDouble(this.longitudesecosgb36stringbuffer.toString()));
            this.longitudedecosgb36stringbuffer.setLength(0);
            this.longitudedecosgb36stringbuffer.append(String.valueOf(this.lambda));
            this.phi = this.convertstringtodec(Double.parseDouble(this.latitudedegosgb36stringbuffer.toString()), Double.parseDouble(this.latitudeminosgb36stringbuffer.toString()), Double.parseDouble(this.latitudesecosgb36stringbuffer.toString()));
            this.latitudedecosgb36stringbuffer.setLength(0);
            this.latitudedecosgb36stringbuffer.append(String.valueOf(this.phi));
            this.bngoldheight = Double.parseDouble(this.bngoldheightstringbuffer.toString());
            this.bngoldheightstringbuffer.setLength(0);
            this.bngoldheightstringbuffer.append(String.valueOf(this.bngoldheight));
        }

    }

    public void convertdectostring(double var1) {
        this.decdegreesdegrees = (double)((int)var1);
        this.decdegreesminutes = StrictMath.floor(StrictMath.abs(60.0D * (StrictMath.abs(var1) - StrictMath.abs(this.decdegreesdegrees))));
        this.decdegreesseconds = (StrictMath.abs(var1) - StrictMath.abs(this.decdegreesdegrees) - this.decdegreesminutes / 60.0D) * 3600.0D;
    }

    public void convertlonglatdecosgb36tolonglatosgb36() {
        if (this.longitudedecosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - longitude λ (deg) must not be empty!");
        } else if (this.latitudedecosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - latitude φ (deg) must not be empty!");
        } else {
            this.longitudedecosgb36 = Double.parseDouble(this.longitudedecosgb36stringbuffer.toString());
            this.latitudedecosgb36 = Double.parseDouble(this.latitudedecosgb36stringbuffer.toString());
            this.convertdectostring(this.longitudedecosgb36);
            this.longitudedegosgb36stringbuffer.setLength(0);
            if (this.longitudedecosgb36 < 0.0D && this.longitudedecosgb36 > -1.0D) {
                this.longitudedegosgb36stringbuffer.append("-");
            }

            this.longitudedegosgb36stringbuffer.append(String.valueOf((int)this.decdegreesdegrees));
            this.longitudeminosgb36stringbuffer.setLength(0);
            this.longitudeminosgb36stringbuffer.append(String.valueOf((int)this.decdegreesminutes));
            this.longitudesecosgb36stringbuffer.setLength(0);
            this.longitudesecosgb36stringbuffer.append(String.valueOf(this.decdegreesseconds));
            this.convertdectostring(this.latitudedecosgb36);
            this.latitudedegosgb36stringbuffer.setLength(0);
            this.latitudedegosgb36stringbuffer.append(String.valueOf((int)this.decdegreesdegrees));
            this.latitudeminosgb36stringbuffer.setLength(0);
            this.latitudeminosgb36stringbuffer.append(String.valueOf((int)this.decdegreesminutes));
            this.latitudesecosgb36stringbuffer.setLength(0);
            this.latitudesecosgb36stringbuffer.append(String.valueOf(this.decdegreesseconds));
        }

    }

    public void convertlonglatosgb36cartesianosgb36() {
        if (this.longitudedecosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - longitude λ (deg) must not be empty!");
        } else if (this.latitudedecosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - latitude φ (deg) must not be empty!");
        } else if (this.bngoldheightstringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - height (m) must not be empty!");
        } else {
            this.longitudedecosgb36 = Double.parseDouble(this.longitudedecosgb36stringbuffer.toString());
            this.lambda = this.longitudedecosgb36;
            this.latitudedecosgb36 = Double.parseDouble(this.latitudedecosgb36stringbuffer.toString());
            this.phi = this.latitudedecosgb36;
            this.phirad = StrictMath.toRadians(this.phi);
            this.lambdarad = StrictMath.toRadians(this.lambda);
            this.ny = this.calculateny(this.ellipsoid_airy1830_a, this.phirad, this.eccentricity2_airy);
            this.heightellipsoid = Double.parseDouble(this.bngoldheightstringbuffer.toString());
            this.bngoldheightstringbuffer.setLength(0);
            this.bngoldheightstringbuffer.append(String.valueOf(this.heightellipsoid));
            this.cartesianxosgb36 = (this.ny + this.heightellipsoid) * StrictMath.cos(this.phirad) * StrictMath.cos(this.lambdarad);
            this.cartesianxosgb36stringbuffer.setLength(0);
            this.cartesianxosgb36stringbuffer.append(String.valueOf(this.cartesianxosgb36));
            this.cartesianyosgb36 = (this.ny + this.heightellipsoid) * StrictMath.cos(this.phirad) * StrictMath.sin(this.lambdarad);
            this.cartesianyosgb36stringbuffer.setLength(0);
            this.cartesianyosgb36stringbuffer.append(String.valueOf(this.cartesianyosgb36));
            this.cartesianzosgb36 = ((1.0D - this.eccentricity2_airy) * this.ny + this.heightellipsoid) * StrictMath.sin(this.phirad);
            this.cartesianzosgb36stringbuffer.setLength(0);
            this.cartesianzosgb36stringbuffer.append(String.valueOf(this.cartesianzosgb36));
        }

    }

    public void convertcartesianosgb36longlatosgb36() {
        if (this.cartesianxosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Cartesian (OSGB36) - X (m) must not be empty!");
        } else if (this.cartesianyosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Cartesian (OSGB36) - Y (m) must not be empty!");
        } else if (this.cartesianzosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Cartesian (OSGB36) - Z (m) must not be empty!");
        } else {
            this.cartesianxosgb36 = Double.parseDouble(this.cartesianxosgb36stringbuffer.toString());
            this.cartesianyosgb36 = Double.parseDouble(this.cartesianyosgb36stringbuffer.toString());
            this.cartesianzosgb36 = Double.parseDouble(this.cartesianzosgb36stringbuffer.toString());
            this.lambdarad = StrictMath.atan(this.cartesianyosgb36 / this.cartesianxosgb36);
            this.lambda = StrictMath.toDegrees(this.lambdarad);
            this.longitudedecosgb36stringbuffer.setLength(0);
            this.longitudedecosgb36stringbuffer.append(String.valueOf(this.lambda));
            this.pdiagxy = StrictMath.sqrt(StrictMath.pow(this.cartesianxosgb36, 2.0D) + StrictMath.pow(this.cartesianyosgb36, 2.0D));
            this.phirad = StrictMath.atan(this.cartesianzosgb36 / this.pdiagxy / (1.0D - this.eccentricity2_airy));
            this.phiold = this.phirad;

            for(int var1 = 0; var1 <= 10000; ++var1) {
                this.ny = this.calculateny(this.ellipsoid_airy1830_a, this.phiold, this.eccentricity2_airy);
                this.phinew = StrictMath.atan((this.cartesianzosgb36 + this.eccentricity2_airy * this.ny * StrictMath.sin(this.phiold)) / this.pdiagxy);
                this.approx = StrictMath.abs(this.phiold - this.phinew);
                this.phiold = this.phinew;
                if (this.approx < 1.0E-12D) {
                    break;
                }
            }

            this.phi = StrictMath.toDegrees(this.phinew);
            this.latitudedecosgb36stringbuffer.setLength(0);
            this.latitudedecosgb36stringbuffer.append(String.valueOf(this.phi));
            this.ny = this.calculateny(this.ellipsoid_airy1830_a, this.phinew, this.eccentricity2_airy);
            this.bngoldheight = this.pdiagxy / StrictMath.cos(this.phinew) - this.ny;
            this.bngoldheightstringbuffer.setLength(0);
            this.bngoldheightstringbuffer.append(String.valueOf(this.bngoldheight));
        }

    }

    void helmerttransformation(boolean var1) {
        if (var1) {
            if (this.cartesianxwgs84stringbuffer.length() == 0) {
                this.showerrormessage("The textfield Cartesian (WGS84) - X (m) must not be empty!");
            } else if (this.cartesianywgs84stringbuffer.length() == 0) {
                this.showerrormessage("The textfield Cartesian (WGS84) - Y (m) must not be empty!");
            } else if (this.cartesianzwgs84stringbuffer.length() == 0) {
                this.showerrormessage("The textfield Cartesian (WGS84) - Z (m) must not be empty!");
            } else {
                this.cartesianxwgs84 = Double.parseDouble(this.cartesianxwgs84stringbuffer.toString());
                this.cartesianywgs84 = Double.parseDouble(this.cartesianywgs84stringbuffer.toString());
                this.cartesianzwgs84 = Double.parseDouble(this.cartesianzwgs84stringbuffer.toString());
                this.helmx = this.cartesianxwgs84;
                this.helmy = this.cartesianywgs84;
                this.helmz = this.cartesianzwgs84;
                this.cartesianxosgb36 = -this.helmtx + (1.0D - this.helms) * this.helmx - this.helmrz * this.helmy + this.helmry * this.helmz;
                this.cartesianyosgb36 = -this.helmty + this.helmrz * this.helmx + (1.0D - this.helms) * this.helmy - this.helmrx * this.helmz;
                this.cartesianzosgb36 = -this.helmtz - this.helmry * this.helmx + this.helmrx * this.helmy + (1.0D - this.helms) * this.helmz;
                this.cartesianxosgb36stringbuffer.setLength(0);
                this.cartesianxosgb36stringbuffer.append(String.valueOf(this.cartesianxosgb36));
                this.cartesianyosgb36stringbuffer.setLength(0);
                this.cartesianyosgb36stringbuffer.append(String.valueOf(this.cartesianyosgb36));
                this.cartesianzosgb36stringbuffer.setLength(0);
                this.cartesianzosgb36stringbuffer.append(String.valueOf(this.cartesianzosgb36));
            }
        } else if (this.cartesianxosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Cartesian (OSGB36) - X (m) must not be empty!");
        } else if (this.cartesianyosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Cartesian (OSGB36) - Y (m) must not be empty!");
        } else if (this.cartesianzosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Cartesian (OSGB36) - Z (m) must not be empty!");
        } else {
            this.cartesianxosgb36 = Double.parseDouble(this.cartesianxosgb36stringbuffer.toString());
            this.cartesianyosgb36 = Double.parseDouble(this.cartesianyosgb36stringbuffer.toString());
            this.cartesianzosgb36 = Double.parseDouble(this.cartesianzosgb36stringbuffer.toString());
            this.helmx = this.cartesianxosgb36;
            this.helmy = this.cartesianyosgb36;
            this.helmz = this.cartesianzosgb36;
            this.cartesianxwgs84 = this.helmtx + (1.0D + this.helms) * this.helmx + this.helmrz * this.helmy - this.helmry * this.helmz;
            this.cartesianywgs84 = this.helmty - this.helmrz * this.helmx + (1.0D + this.helms) * this.helmy + this.helmrx * this.helmz;
            this.cartesianzwgs84 = this.helmtz + this.helmry * this.helmx - this.helmrx * this.helmy + (1.0D + this.helms) * this.helmz;
            this.cartesianxwgs84stringbuffer.setLength(0);
            this.cartesianxwgs84stringbuffer.append(String.valueOf(this.cartesianxwgs84));
            this.cartesianywgs84stringbuffer.setLength(0);
            this.cartesianywgs84stringbuffer.append(String.valueOf(this.cartesianywgs84));
            this.cartesianzwgs84stringbuffer.setLength(0);
            this.cartesianzwgs84stringbuffer.append(String.valueOf(this.cartesianzwgs84));
        }

    }

    public void convertcartesianwgs84longlatwgs84() {
        if (this.cartesianxwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Cartesian (WGS84) - X (m) must not be empty!");
        } else if (this.cartesianywgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Cartesian (WGS84) - Y (m) must not be empty!");
        } else if (this.cartesianzwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Cartesian (WGS84) - Z (m) must not be empty!");
        } else {
            this.cartesianxwgs84 = Double.parseDouble(this.cartesianxwgs84stringbuffer.toString());
            this.cartesianywgs84 = Double.parseDouble(this.cartesianywgs84stringbuffer.toString());
            this.cartesianzwgs84 = Double.parseDouble(this.cartesianzwgs84stringbuffer.toString());
            this.lambdarad = StrictMath.atan(this.cartesianywgs84 / this.cartesianxwgs84);
            this.lambda = StrictMath.toDegrees(this.lambdarad);
            this.longitudedecwgs84stringbuffer.setLength(0);
            this.longitudedecwgs84stringbuffer.append(String.valueOf(this.lambda));
            this.pdiagxy = StrictMath.sqrt(StrictMath.pow(this.cartesianxwgs84, 2.0D) + StrictMath.pow(this.cartesianywgs84, 2.0D));
            this.phirad = StrictMath.atan(this.cartesianzwgs84 / this.pdiagxy / (1.0D - this.eccentricity2_wgs84));
            this.phiold = this.phirad;

            for(int var1 = 0; var1 <= 10000; ++var1) {
                this.ny = this.calculateny(this.ellipsoid_wgs84_a, this.phiold, this.eccentricity2_wgs84);
                this.phinew = StrictMath.atan((this.cartesianzwgs84 + this.eccentricity2_wgs84 * this.ny * StrictMath.sin(this.phiold)) / this.pdiagxy);
                this.approx = StrictMath.abs(this.phiold - this.phinew);
                this.phiold = this.phinew;
                if (this.approx < 1.0E-12D) {
                    break;
                }
            }

            this.phi = StrictMath.toDegrees(this.phinew);
            this.latitudedecwgs84stringbuffer.setLength(0);
            this.latitudedecwgs84stringbuffer.append(String.valueOf(this.phi));
            this.ny = this.calculateny(this.ellipsoid_wgs84_a, this.phinew, this.eccentricity2_wgs84);
            this.heightdecwgs84 = this.pdiagxy / StrictMath.cos(this.phinew) - this.ny;
            this.heightdecwgs84stringbuffer.setLength(0);
            this.heightdecwgs84stringbuffer.append(String.valueOf(this.heightdecwgs84));
        }

    }

    public void convertlonglatwgs84cartesianwgs84() {
        if (this.longitudedecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - longitude λ (deg) must not be empty!");
        } else if (this.latitudedecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - latitude φ (deg) must not be empty!");
        } else if (this.heightdecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - height (m) must not be empty!");
        } else {
            this.longitudedecwgs84 = Double.parseDouble(this.longitudedecwgs84stringbuffer.toString());
            this.lambda = this.longitudedecwgs84;
            this.latitudedecwgs84 = Double.parseDouble(this.latitudedecwgs84stringbuffer.toString());
            this.phi = this.latitudedecwgs84;
            this.phirad = StrictMath.toRadians(this.phi);
            this.lambdarad = StrictMath.toRadians(this.lambda);
            this.ny = this.calculateny(this.ellipsoid_wgs84_a, this.phirad, this.eccentricity2_wgs84);
            this.heightdecwgs84 = Double.parseDouble(this.heightdecwgs84stringbuffer.toString());
            this.heightdecwgs84stringbuffer.setLength(0);
            this.heightdecwgs84stringbuffer.append(String.valueOf(this.heightdecwgs84));
            this.cartesianxwgs84 = (this.ny + this.heightdecwgs84) * StrictMath.cos(this.phirad) * StrictMath.cos(this.lambdarad);
            this.cartesianywgs84 = (this.ny + this.heightdecwgs84) * StrictMath.cos(this.phirad) * StrictMath.sin(this.lambdarad);
            this.cartesianzwgs84 = ((1.0D - this.eccentricity2_wgs84) * this.ny + this.heightdecwgs84) * StrictMath.sin(this.phirad);
            this.cartesianxwgs84stringbuffer.setLength(0);
            this.cartesianxwgs84stringbuffer.append(String.valueOf(this.cartesianxwgs84));
            this.cartesianywgs84stringbuffer.setLength(0);
            this.cartesianywgs84stringbuffer.append(String.valueOf(this.cartesianywgs84));
            this.cartesianzwgs84stringbuffer.setLength(0);
            this.cartesianzwgs84stringbuffer.append(String.valueOf(this.cartesianzwgs84));
        }

    }

    public void convertlonglatdecwgs84tolonglatwgs84() {
        if (this.longitudedecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - longitude λ (deg) must not be empty!");
        } else if (this.latitudedecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - latitude φ (deg) must not be empty!");
        } else {
            this.longitudedecwgs84 = Double.parseDouble(this.longitudedecwgs84stringbuffer.toString());
            this.latitudedecwgs84 = Double.parseDouble(this.latitudedecwgs84stringbuffer.toString());
            this.convertdectostring(this.longitudedecwgs84);
            this.longitudedegwgs84stringbuffer.setLength(0);
            if (this.longitudedecwgs84 < 0.0D && this.longitudedecwgs84 > -1.0D) {
                this.longitudedegwgs84stringbuffer.append("-");
            }

            this.longitudedegwgs84stringbuffer.append(String.valueOf((int)this.decdegreesdegrees));
            this.longitudeminwgs84stringbuffer.setLength(0);
            this.longitudeminwgs84stringbuffer.append(String.valueOf((int)this.decdegreesminutes));
            this.longitudesecwgs84stringbuffer.setLength(0);
            this.longitudesecwgs84stringbuffer.append(String.valueOf(this.decdegreesseconds));
            this.convertdectostring(this.latitudedecwgs84);
            this.latitudedegwgs84stringbuffer.setLength(0);
            this.latitudedegwgs84stringbuffer.append(String.valueOf((int)this.decdegreesdegrees));
            this.latitudeminwgs84stringbuffer.setLength(0);
            this.latitudeminwgs84stringbuffer.append(String.valueOf((int)this.decdegreesminutes));
            this.latitudesecwgs84stringbuffer.setLength(0);
            this.latitudesecwgs84stringbuffer.append(String.valueOf(this.decdegreesseconds));
        }

    }

    public void convertlonglatwgs84tolonglatdecwgs84() {
        if (this.longitudedegwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - longitude λ (deg) must not be empty!");
        } else if (this.longitudeminwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - longitude φ (min) must not be empty!");
        } else if (this.longitudesecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - longitude φ (sec) must not be empty!");
        } else if (this.latitudedegwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - latitude φ (deg) must not be empty!");
        } else if (this.latitudeminwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - latitude φ (min) must not be empty!");
        } else if (this.latitudesecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - latitude φ (sec) must not be empty!");
        } else if (this.heightdecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - height (m) must not be empty!");
        } else {
            this.lambda = this.convertstringtodec(Double.parseDouble(this.longitudedegwgs84stringbuffer.toString()), Double.parseDouble(this.longitudeminwgs84stringbuffer.toString()), Double.parseDouble(this.longitudesecwgs84stringbuffer.toString()));
            this.longitudedecwgs84stringbuffer.setLength(0);
            this.longitudedecwgs84stringbuffer.append(String.valueOf(this.lambda));
            this.phi = this.convertstringtodec(Double.parseDouble(this.latitudedegwgs84stringbuffer.toString()), Double.parseDouble(this.latitudeminwgs84stringbuffer.toString()), Double.parseDouble(this.latitudesecwgs84stringbuffer.toString()));
            this.latitudedecwgs84stringbuffer.setLength(0);
            this.latitudedecwgs84stringbuffer.append(String.valueOf(this.phi));
            this.heightdecwgs84 = Double.parseDouble(this.heightdecwgs84stringbuffer.toString());
            this.heightdecwgs84stringbuffer.setLength(0);
            this.heightdecwgs84stringbuffer.append(String.valueOf(this.heightdecwgs84));
        }

    }

    public void convertdbxtolatlongwgs84() {
        if (this.longitudedbxstringbuffer.length() == 0) {
            this.showerrormessage("The textfield DBX (WGS84) - longitude λ must not be empty!");
        } else if (this.latitudedbxstringbuffer.length() == 0) {
            this.showerrormessage("The textfield DBX (WGS84) - latitude φ must not be empty!");
        } else if (this.heightdecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - height (m) must not be empty!");
        } else {
            this.lambda = Double.parseDouble(this.longitudedbxstringbuffer.toString()) / this.dbxconst;
            this.longitudedecwgs84stringbuffer.setLength(0);
            this.longitudedecwgs84stringbuffer.append(String.valueOf(this.lambda));
            this.phi = Double.parseDouble(this.latitudedbxstringbuffer.toString()) / this.dbxconst;
            this.latitudedecwgs84stringbuffer.setLength(0);
            this.latitudedecwgs84stringbuffer.append(String.valueOf(this.phi));
            this.heightdecwgs84 = Double.parseDouble(this.heightdecwgs84stringbuffer.toString());
            this.heightdecwgs84stringbuffer.setLength(0);
            this.heightdecwgs84stringbuffer.append(String.valueOf(this.heightdecwgs84));
        }

    }

    public void convertlatlongwgs84todbx() {
        if (this.longitudedecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - longitude λ (deg) must not be empty!");
        } else if (this.latitudedecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - latitude φ (deg) must not be empty!");
        } else if (this.heightdecwgs84stringbuffer.length() == 0) {
            this.showerrormessage("The textfield Long,Lat (WGS84) - height (m) must not be empty!");
        } else {
            this.longitudedecwgs84 = Double.parseDouble(this.longitudedecwgs84stringbuffer.toString());
            this.longitudedbx = StrictMath.round(this.longitudedecwgs84 * this.dbxconst);
            this.longitudedbxstringbuffer.setLength(0);
            this.longitudedbxstringbuffer.append(String.valueOf(this.longitudedbx));
            this.latitudedecwgs84 = Double.parseDouble(this.latitudedecwgs84stringbuffer.toString());
            this.latitudedbx = StrictMath.round(this.latitudedecwgs84 * this.dbxconst);
            this.latitudedbxstringbuffer.setLength(0);
            this.latitudedbxstringbuffer.append(String.valueOf(this.latitudedbx));
            this.heightdecwgs84 = Double.parseDouble(this.heightdecwgs84stringbuffer.toString());
            this.heightdecwgs84stringbuffer.setLength(0);
            this.heightdecwgs84stringbuffer.append(String.valueOf(this.heightdecwgs84));
        }

    }

    public void calcconvandscale() {
        if (this.longitudedecosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - longitude λ (deg) must not be empty!");
        } else if (this.latitudedecosgb36stringbuffer.length() == 0) {
            this.showerrormessage("The textfield BNG (OSGB36) - latitude φ (deg) must not be empty!");
        } else {
            this.longitudedecosgb36 = Double.parseDouble(this.longitudedecosgb36stringbuffer.toString());
            this.latitudedecosgb36 = Double.parseDouble(this.latitudedecosgb36stringbuffer.toString());
            this.longitudedecosgb36rad = StrictMath.toRadians(this.longitudedecosgb36);
            this.latitudedecosgb36rad = StrictMath.toRadians(this.latitudedecosgb36);
            this.ny = this.calculateny(this.ellipsoid_airy1830_af, this.latitudedecosgb36rad, this.eccentricity2_airy);
            this.rho = this.calculaterho(this.ny, this.latitudedecosgb36rad);
            this.eta2 = this.calculateeta2(this.ny, this.rho);
            this.XIII = StrictMath.sin(this.latitudedecosgb36rad);
            this.XIV = StrictMath.sin(this.latitudedecosgb36rad) * StrictMath.pow(StrictMath.cos(this.latitudedecosgb36rad), 2.0D) / 3.0D * (1.0D + 3.0D * this.eta2 + 2.0D * StrictMath.pow(this.eta2, 2.0D));
            this.XV = StrictMath.sin(this.latitudedecosgb36rad) * StrictMath.pow(StrictMath.cos(this.latitudedecosgb36rad), 4.0D) / 15.0D * (2.0D - StrictMath.pow(StrictMath.tan(this.latitudedecosgb36rad), 2.0D));
            this.Ptruelambda = this.longitudedecosgb36rad - StrictMath.toRadians(this.lambda0);
            this.convergenceosgb36 = StrictMath.toDegrees(this.Ptruelambda * this.XIII + StrictMath.pow(this.Ptruelambda, 3.0D) * this.XIV + StrictMath.pow(this.Ptruelambda, 5.0D) * this.XV);
            this.convergenceosgb36stringbuffer.setLength(0);
            this.convergenceosgb36stringbuffer.append(String.valueOf(this.convergenceosgb36));
            this.XIX = StrictMath.pow(StrictMath.cos(this.latitudedecosgb36rad), 2.0D) / 2.0D * (1.0D + this.eta2);
            this.XX = StrictMath.pow(StrictMath.cos(this.latitudedecosgb36rad), 4.0D) / 24.0D * (5.0D - 4.0D * StrictMath.pow(StrictMath.tan(this.latitudedecosgb36rad), 2.0D) + 14.0D * this.eta2 - 28.0D * StrictMath.pow(StrictMath.tan(this.latitudedecosgb36rad), 2.0D) * this.eta2);
            this.localscalefactorosgb36 = this.projection_bng_f0 * (1.0D + StrictMath.pow(this.Ptruelambda, 2.0D) * this.XIX + StrictMath.pow(this.Ptruelambda, 4.0D) * this.XX);
            this.localscalefactorosgb36stringbuffer.setLength(0);
            this.localscalefactorosgb36stringbuffer.append(String.valueOf(this.localscalefactorosgb36));
        }

    }

    public void showresults() {
    }
}
