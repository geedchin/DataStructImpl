import java.util.Scanner;

public class RB_TREE {
    
    private Node root = null;
    
    public RB_TREE() {
        
    }
    
    //TEST
    public static void main(String[] args) {
        
        RB_TREE  rbtree = new RB_TREE();
    
        Scanner sc = new Scanner(System.in);
        int oper = 0;
        while(true){
            System.out.println("选择操作：\n1、新增\n2、删除\n3、前序遍历\n4、中序遍历\n5、后序遍历\n\n0、退出");
            oper = sc.nextInt();
            switch (oper){
                case 1:{
                    System.out.println("输入添加的数字：");
                    int input = sc.nextInt();
                    rbtree.put(input);
                    System.out.println("添加成功");
                };break;
                case 2:{
                    System.out.println("输入删除的数字：");
                    int input = sc.nextInt();
                    rbtree.remove(input);
                    System.out.println("删除成功");
                };break;
                case 3:rbtree.DLR();break;
                case 4:rbtree.LDR();break;
                case 5:rbtree.LRD();break;
                case 0:System.exit(0);break;
                default:continue;
            }
        }
        
    }
    
    /**
     * 红黑树性质（满足平衡二叉树的前提下）：
     * 1、所有节点要么为黑色，要么为红色
     * 2、根节点为黑色
     * 3、所有叶子节点为黑色（null视为叶子节点）
     * 4、红色节点的子节点为黑色
     * 5、对每个节点，从其到达叶子的所有路径中黑色节点个数相同
     * _ 
     * 向红黑树中插入一个节点后可能会出现的情况（其中新节点记为红色，先将新节点插入到叶节点位置，此时可能破坏红黑树性质，执行fixRBTree函数）
     * （插入过程默认原树已经满足红黑树性质，部分地方未做异常校验，如果树不满足红黑树性质，可能会出异常）
     * fixRBTree(z)
     * _ 情况1：z为根节点：将z.color设置为black，满足红黑树性质，结束
     * _ 情况2：z的父节点为黑色，满足红黑树性质，结束
     * _ 情况3：z的父节点为红色，叔节点为红色，祖父节点为黑色：设置父、叔节点为黑色、祖父节点为红色，再令z指向祖父节点，递归fixRBTree(z)，然后返回
     * _ 
     * _ 情况4：z的父节点为红色，叔节点为黑色，祖父节点为黑色
     * _    情况4.1 z的父节点为祖父节点的左孩子
     * _        情况4.1.1 z为父节点的右孩子：z绕z的父节点左旋(z的父节点变成z的左子节点)，令z指向z的左子节点=》情况4.1.2
     * _        情况4.1.2 z为父节点的左孩子：将z的父节点、祖父节点变色，使z的父节点绕z的祖父节点右旋(此时，父节点为黑色，满足情况2，结束)
     * _ 
     * _    情况4.2 z的父节点为祖父节点的右孩子
     * _        情况4.2.1 z为父节点的左孩子：z绕z的父节点右旋(z的父节点变成z的右子节点)，令z指向z的右子节点=》情况4.2.2
     * _        情况4.2.2 z为父节点的右孩子：将z的父节点、祖父节点变色，使z的父节点绕z的祖父节点左旋(此时，父节点为黑色，满足情况2，结束)
     *
     * _ 
     * 分析：情况1、2、4.x均为结束情况。只有情况3递归调用函数，而且递归的过程中z向上移动两级，因此z向上的复杂度为 O(lgn)
     * 分析：有旋转转的地方只有情况4.x，且无论哪次到情况4.x，最多旋转两次就结束
     * 分析：因此，插入节点复杂度为：将z插入叶子节点的复杂度O(lgn) + 递归复杂度 O(lgn) + 旋转复杂度O(1)  =  O(lgn)
     */
    /**
     * INSERT
     ***/
    public void put(int val) {
        
        Node ins = new Node(val);
        
        Node par = null, np = null, z = ins;
        if (root == null) {
            root = ins;
        } else {
            np = root;
            while (np != null) {
                par = np;
                if (np.val > ins.val) {
                    np = np.left;
                } else {
                    np = np.right;
                }
            }
            ins.parent = par;
            if (par.val > ins.val) {
                par.left = ins;
            } else {
                par.right = ins;
            }
        }
        RB_INSERT_FIXUP(z);
    }
    
    private void RB_INSERT_FIXUP(Node z) {
        
        // case1  z is root of tree
        if (z == root) {
            setBLACK(z);
            return;
        }
        // case2  z.parent is black
        if (isBLACK(z.parent)) return;
        // case3  z.uncle and z.parent is red and z.grandfather is black
        if (isRED(uncleOf(z)) && isRED(z.parent) && isBLACK(z.parent.parent)) {
            setBLACK(uncleOf(z));
            setBLACK(z.parent);
            setRED(z.parent.parent);
            RB_INSERT_FIXUP(z.parent.parent);
            return;
        }
        // case4 z.uncle is black and z.parent is red and z.grandfather is black(the third is always true beacuse of z.uncle's color)
        // case 4.1 z.parent is left of z.grandfather
        if (z.parent.parent.left == z.parent) {
            // case 4.1.1 z is right of z.parent
            if (z.parent.right == z) {
                RotateLeft(z);
                z = z.left;// be case 4.1.2
            }
            // case 4.1.2 z is left of z.parent
            setBLACK(z.parent);
            setRED(z.parent.parent);
            RotateRight(z.parent);
        } else {// case 4.2 z.parent is right of z.grandfather
            // case 4.2.1 z is left of z.parent
            if (z.parent.left == z) {
                RotateRight(z);
                z = z.right;    // be case 4.2.2
            }
            // case 4.2.2 z is right of parent
            setBLACK(z.parent);
            setRED(z.parent.parent);
            RotateLeft(z.parent);
        }
    }
    /**     END INSERT  ***/
    /**
     * REMOVE
     ***/
    
    public boolean remove(int val) {
        
        Node p = root;
        while (p != null) {
            if (val == p.val) break;
            else if (val < p.val) p = p.left;
            else p = p.right;
        }
        if (p == null) return false;
        RB_DELETE(p);
        return true;
    }
    
    /**
     * 结论1：若z 有且仅有一个非空子节点，那么z 必为黑色，且子节点为红色，且子节点只有一层
     * <p>
     * 情况1 ：z.left  z.right 均不为空，此时找到 z 的后继节点，记为zNext（必无左子节点），将zNext与z替换，此时删除z即可（此时z无左节点，转入情况3、4）
     * 情况2 ：z.left 不为空      （根据结论1，z为黑色，且z.left  为红色，且z.left  无子节点）此时直接用z.left  替换掉z，并将z.left  染黑
     * 情况3 ：z.right 不为空     （根据结论1，z为黑色，且z.right 为红色，且z.right 无子节点）此时直接用z.right 替换掉z，并将z.right 染黑
     * 情况4 ：z.left z.right均为空  fixup
     */
    private void RB_DELETE(Node z) {
        
        Node zNext = null;
        if (z.left != null && z.right != null) {    // 经过处理之后，z 不含左子节点（右子节点存在性不确定）
            // 找到z 的后继，并将z的后继放至z处（此时删除z后继位置节点即可，而且此时，z的后继必无左子节点）
            zNext = TREE_MINIMUM(z.right);
            z.val = zNext.val;
            z = zNext;
        }
        
        Node replacement = (z.left == null) ? z.right : z.left;
        if (replacement != null) {// 情况2、3
            replacement.parent = z.parent;
            if (z.parent == null)
                root = replacement;
            else if (z == z.parent.left)
                z.parent.left = replacement;
            else
                z.parent.right = replacement;
            // z为删除的点，将其从树种摘除
            z.left = z.right = z.parent = null;
            
            // 修正错误
            if (isBLACK(z))
                RB_DELETE_FIXUP(replacement);
        } else if (z.parent == null) { 
            root = null;
        } else { //  情况4
            if (z.color == BLACK)
                RB_DELETE_FIXUP(z);
            // 摘除节点z
            if (z.parent != null) {
                if (z == z.parent.left)
                    z.parent.left = null;
                else if (z == z.parent.right)
                    z.parent.right = null;
                z.parent = null;
            }
        }
        
    }
    
    /**
     * 修复红黑树：
     * 
     * 情况1 ：x 为 x.parent 左子节点
     * _    情况1.1 ：x 兄弟节点为红色（此时父节点必为黑，x兄弟节点的两子节点为黑）：兄弟节点置黑，父节点置红，兄弟节点绕父节点左旋（x兄弟节点的左节点变为x新的兄弟节点）。此时转为情况1.2
     * _    情况1.2 ：x 兄弟节点为黑色 
     * _        情况1.2.1 ：x 的兄弟节点左右孩子均为黑色：将x兄弟节点置红，x指向x 的父节点
     * _        情况1.2.2 ：x 的兄弟节点仅右孩子节点为黑色：将x兄弟节点置红，x兄弟节点左孩子置黑，将x.brother.left 绕 x.brother右旋。（转入情况1.2.3）
     * _        情况1.2.3 ：x 的兄弟节点仅左孩子节点为黑色：x兄弟节点置红，x父节点置黑，将x 节点，将x.brother 绕 x.parent 左旋(新x = root结束)
     * _
     * 情况2 ：x 为x.parent 右子节点（情况同上，左右相反）
     * _
     * _
     */
    private void RB_DELETE_FIXUP(Node x) {
        while (x != root && isBLACK(x)) {
            if (x == x.parent.left) {
                Node sib = x.parent.right;
                
                if (isRED(sib)) {
                    setBLACK(sib);
                    setRED(x.parent);
                    RotateLeft(sib);
                    sib = x.parent.right;
                }
                
                if (isBLACK(sib.left) &&
                        isBLACK(sib.right)) {
                    setRED(sib);
                    x = x.parent;
                } else {
                    if (isBLACK(sib.right)) {
                        setBLACK(sib.left);
                        setRED(sib);
                        RotateRight(sib.left);
                        sib = x.parent.right;
                    }
                    sib.color = x.parent.color;
                    setBLACK(x.parent);
                    setBLACK(sib.right);
                    RotateLeft(x.parent.right);
                    x = root;
                }
            } else { // symmetric
                Node sib = x.parent.left;
                
                if (isRED(sib)) {
                    setBLACK(sib);
                    setRED(x.parent);
                    RotateRight(sib);
                    sib = x.parent.left;
                }
                
                if (isBLACK(sib.right) &&
                        isBLACK(sib.left)) {
                    setRED(sib);
                    x = x.parent;
                } else {
                    if (isBLACK(sib.left)) {
                        setBLACK(sib.right);
                        setRED(sib);
                        RotateLeft(sib.right);
                        sib = x.parent.left;
                    }
                    sib.color = x.parent.color;
                    setBLACK(x.parent);
                    setBLACK(sib.left);
                    RotateRight(sib);
                    x = root;
                }
            }
        }
        setBLACK(x);
    }
    
    /**
     * 命名变量n 为当前节点，
     * 情况1：节点n 兄弟节点红色（根据红黑树性质，x.parent 为黑色，左(右)旋，兄弟节点及父节点变色）=》情况2
     * 情况2：节点n 兄弟节点w 为黑色，父节点红色（父节点染黑，兄弟节点染红）=》结束后  删除 x 节点
     * 情况3：节点n 兄弟节点w 为黑色，父节点黑色
     */
//    private void RB_DELETE_FIXUP(Node x) {
//        //while()
//    }
    private Node TREE_MINIMUM(Node z) {
        while (z.left != null) {
            z = z.left;
        }
        return z;
    }
    /**     END REMOVE  ***/
    
    
    /*** R2L     T
     *           |
     *          Gra
     *         /   \
     *        A    P
     *       / \  / \
     *      x  x l  r
     *
     *      此处旋转默认p的父节点一定存在（内部调用，旋转的时候已经确定Gra节点存在了）
     * ***/
    private void RotateLeft(Node p) {
        if (p == null || p.parent.left == p) return;
        Node l = p.left;
        Node T = p.parent.parent;
        setRight(p.parent, l);   // 将p左节点设置为gra右节点 ①
        setParent(l, p.parent);  // 将p左节点设置为gra右节点 ②
        setLeft(p, p.parent);    // 将p的父节点设置为p的左节点 ①
        setParent(p.parent, p);  // 将p的父节点设置为p的左节点 ②
        setParent(p, T);
        if (T == null) {        // 如果T为空,说明 p.parent为根节点，将p 赋值给root，并将root 的parent置空
            root = p;
            //root.parent=null;
        } else if (T.left == p.left) {
            T.left = p;
        } else {
            T.right = p;
        }
    }
    
    /**
     * L2R
     *
     * @param p
     */
    private void RotateRight(Node p) {
        if (p == null || p.parent.right == p) return;
        Node r = p.right;
        Node T = p.parent.parent;
        setLeft(p.parent, r);       // 将p右节点设置为gra左节点 ①
        setParent(r, p.parent);     // 将p右节点设置为gra左节点 ②
        setRight(p, p.parent);      // 将p的父节点设置为p的右节点 ①
        setParent(p.parent, p);     // 将p的父节点设置为p的右节点 ②
        setParent(p, T);
        if (T == null) {        // 如果T为空,说明 p.parent为根节点，将p 赋值给root，并将root 的parent置空
            root = p;
            //p.parent = null;
        } else if (T.left == p.right) {
            T.left = p;
        } else {
            T.right = p;
        }
    }
    
    
    private static final class Node {
        protected Node left = null;
        protected Node right = null;
        protected Node parent = null;
        
        protected int val;
        
        protected boolean color = RED;
        
        Node(int k) {
            this.val = k;
        }
    }
    
    private void setLeft(Node par, Node ch) {
        if (par == null) return;
        par.left = ch;
    }
    
    private void setRight(Node par, Node ch) {
        if (par == null) return;
        par.right = ch;
    }
    
    private void setParent(Node ch, Node par) {
        if (ch == null) return;
        ch.parent = par;
    }
    
    private Node uncleOf(Node p) {
        if (p.parent.parent.left == p.parent) return p.parent.parent.right;
        return p.parent.parent.left;
    }
    
    private boolean isRED(Node p) {
        if (p == null) return false;
        return p.color == RED;
    }
    
    private boolean isBLACK(Node p) {
        return !isRED(p);
    }
    
    private void setBLACK(Node p) {
        if (p == null) return;
        p.color = BLACK;
    }
    
    private void setRED(Node p) {
        p.color = RED;
    }
    
    private boolean isRight(Node p) {
        if (p.parent.right == p) return true;
        return false;
    }
    
    private boolean isLeft(Node p) {
        return !isRight(p);
    }
    
    private Node brotherOf(Node p) {
        if (isLeft(p)) return p.parent.right;
        return p.parent.left;
    }
    
    private Node getNode(int k) {
        
        Node p = root;
        while (p != null) {
            if (k < p.val) p = p.left;
            else if (k > p.val) p = p.right;
            else return p;
        }
        return null;
    }
    
    
    private static boolean RED = false;
    private static boolean BLACK = true;
    
    /**
     * 先序遍历
     */
    public void DLR() {
        
        System.out.println("FIRST_ORDER");
        System.out.print("\t");
        DLR0(root);
        System.out.println();
    }
    
    private void DLR0(Node p) {
        if (p == null) return;
        System.out.print("[ " + p.val + (isBLACK(p) ? "_BLACK" : "_RED") + " ]" + " => ");
        DLR0(p.left);
        DLR0(p.right);
    }
    /**
     * 中序遍历
     */
    public void LDR() {
        
        System.out.println("MID_ORDER");
        System.out.print("\t");
        LDR0(root);
        System.out.println();
    }
    
    private void LDR0(Node p) {
        if (p == null) return;
        LDR0(p.left);
        System.out.print("[ " + p.val + (isBLACK(p) ? "_BLACK" : "_RED") + " ]" + " => ");
        LDR0(p.right);
    }
    /**
     * 后序遍历
     */
    public void LRD() {
        
        System.out.println("LAST_ORDER");
        System.out.print("\t");
        LRD0(root);
        System.out.println();
    }
    
    private void LRD0(Node p) {
        if (p == null) return;
        LRD0(p.left);
        LRD0(p.right);
        System.out.print("[ " + p.val + (isBLACK(p) ? "_BLACK" : "_RED") + " ]" + " => ");
    }
    
}